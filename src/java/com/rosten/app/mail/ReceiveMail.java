package com.rosten.app.mail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Flags;
import javax.mail.internet.MimeUtility;

public class ReceiveMail {
	private String account;
	private String password;
	private String pop3Host;
	private String pop3Port;
	private Store store;
	
	
	private ReceiveMail(String account, String password, String pop3Host,String pop3Port) {
		this.account = account;
		this.password = password;
		this.pop3Host = pop3Host;
		this.pop3Port = pop3Port;
	}
	
	/*
	 * 获取信息体(数组),并打印出其具体信息
	 */
	public Message[] getMessage() throws Exception {
		// 获取收件箱
		try {
			// INBOX 来表示“此服务器上此用户的主文件夹”。
			Folder inbox = getStore().getFolder("INBOX");
			// READ_WRITE 此文件夹的状态和内容可以修改
			inbox.open(Folder.READ_WRITE);
			// 得到INBOX里的所有信息,messages是所有信息个数组集合
			Message[] messages = inbox.getMessages();
			return messages;
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取邮箱的存储对象
	 */
	public Store getStore() {
		if (this.store == null || !this.store.isConnected()) {
			try {
				Properties props = new Properties();
				
				props.setProperty("mail.store.protocol", "pop3");    
		        props.setProperty("mail.pop3.host", getPop3Host());    
		        props.setProperty("mail.pop3.port", getPop3Port());
		        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
				
				// 创建mail的session
		        Session session = Session.getInstance(props);
		        session.setDebug(false); //是否启用debug模式
		        Store store = session.getStore("pop3");
		        store.connect(getAccount(), getPassword());
				this.store = store;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return this.store;
	}
	/**
	 * @return 获取附件名称
	 */
	public List<String> getFiles(Message m) throws MessagingException,IOException {
		List<String> files = new ArrayList<String>();
		if (m.isMimeType("multipart/mixed")) {
			Multipart mp = (Multipart) m.getContent();
			// 得到邮件内容的Multipart对象并得到内容中的part的数量
			int count = mp.getCount();
			for (int i = 1; i < count; i++) {
				// 获取附件
				Part part = mp.getBodyPart(i);
				// 获取邮件附件名
				String serverFileName = MimeUtility.decodeText(part.getFileName());
				// 生成UUID作为在本地系统中唯一的文件标识
				// String fileName=UUID.randomUUID().toString();
				// File file=new File(fileName+getFileSuffix(fileName));
				// //读写文件
				// FileOutputStream fos=new FileOutputStream(file);
				// InputStream is=part.getInputStream();
				// BufferedOutputStream outs=new BufferedOutputStream(fos);
				// //使用IO流读取邮件附件
				// byte[] b=new byte[1024];
				// int hasRead=0;
				// while((hasRead=is.read(b))>0){
				// outs.write(b,0,hasRead);
				// }
				// outs.close();
				// is.close();
				// fos.close();
				// 封装对象
				files.add(serverFileName);
			}
		}
		return files;
	}
	
	/**
	 * 获取发送人
	 */
	public String getSender(Message m) throws UnsupportedEncodingException,
			MessagingException {
		Address[] addresses = m.getFrom();
		return MimeUtility.decodeText(addresses[0].toString());
	}
	
	/**
	 * 是否已经读取
	 */
	public boolean hasRead(Message m) throws MessagingException {
		Flags flags = m.getFlags();
		return flags.contains(Flags.Flag.SEEN);
	}

	/**
	 * 获取所有接收人
	 */
	public List<String> getAllRecipients(Message m) throws MessagingException {
		Address[] addresses = m.getAllRecipients();
		return getAddresses(addresses);
	}
	
	/**
	 * 处理邮件正文的工具方法 A
	 */
	private StringBuffer getContent(Part part, StringBuffer result)
			throws MessagingException, IOException {
		if (part.isMimeType("multipart/*")) {
			Multipart p = (Multipart) part.getContent();
			int count = p.getCount();
			if (count > 1)
				count = 1;
			for (int i = 0; i < count; i++) {
				BodyPart bp = p.getBodyPart(i);
				getContent(bp, result);
			}
		} else if (part.isMimeType("text/*")) {
			result.append(part.getContent());
		}
		return result;
	}

	/**
	 * 获取内容 A
	 */
	@SuppressWarnings("unused")
	private String getContent(Message m) throws MessagingException, IOException {
		StringBuffer sb = new StringBuffer("");
		return getContent(m, sb).toString();
	}
	
	/**
	 * @return 获取抄送人
	 */
	public List<String> getCC(Message m) throws MessagingException {
		Address[] addresses = m.getRecipients(Message.RecipientType.CC);
		return getAddresses(addresses);
	}
	
	/**
	 * @return 获取接收日期.先获取发送日期,如果为空就获取接收日期,还没就当前日期.
	 */
	public Date getReceivedDate(Message m) throws MessagingException {
		if (m.getSentDate() != null) {
			return m.getSentDate();
		}
		if (m.getReceivedDate() != null) {
			return m.getReceivedDate();
		}
		return new Date();
	}
	
	/**
	 * @return 获取到地址
	 */
	private List<String> getAddresses(Address[] addresses) {
		List<String> result = new ArrayList<String>();
		if (addresses == null) {
			return result;
		}
		for (Address str : addresses) {
			result.add(str.toString());
		}
		return result;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPop3Host() {
		return pop3Host;
	}

	public void setPop3Host(String pop3Host) {
		this.pop3Host = pop3Host;
	}

	public String getPop3Port() {
		return pop3Port;
	}

	public void setPop3Port(String pop3Port) {
		this.pop3Port = pop3Port;
	}
}
