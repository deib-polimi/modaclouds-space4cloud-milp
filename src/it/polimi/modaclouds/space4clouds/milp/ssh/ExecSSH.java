package it.polimi.modaclouds.space4clouds.milp.ssh;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import it.polimi.modaclouds.space4clouds.milp.ssh.ScpTo.MyUserInfo;
import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;

//this class allows to execute commands on AMPL server
public class ExecSSH {

	// login to AMPL server
	public String ScpUserName = "";
	// AMPL server's address
	public String ScpHost = "";
	// password for account on AMPL server
	public String ScpPasswd = "";
	// directory on server with model (file model.mod)
	public String UploadPath = "";

	// constructor
	public ExecSSH(ClassOptions CurrOptions) {
		ScpUserName = CurrOptions.SSHUserName;
		ScpHost = CurrOptions.SSHhost;
		ScpPasswd = CurrOptions.SSHPassword;
		UploadPath = CurrOptions.UploadPath;
	}

	// main execution function
	// returns in List<Strings> all answers of the server
	public List<String> mainExec() {
		List<String> res = new ArrayList<String>();
		try {
			// creating session with username, server's address and port (22 by
			// default)
			JSch jsch = new JSch();
			Session session = jsch.getSession(ScpUserName, ScpHost, 22);

			// receiving user password if it was not collected before
			if (ScpPasswd == "")
				ScpPasswd = JOptionPane.showInputDialog("Enter password");
			session.setPassword(ScpPasswd);

			// this class sets visual forms for interactions with users
			// required by implementation
			UserInfo ui = new MyUserInfo() {
				public void showMessage(String message) {
					JOptionPane.showMessageDialog(null, message);
				}

				public boolean promptYesNo(String message) {
					Object[] options = { "yes", "no" };
					int foo = JOptionPane.showOptionDialog(null, message,
							"Warning", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
					return foo == 0;
				}
			};
			session.setUserInfo(ui);

			// disabling of certificate checks
			session.setConfig("StrictHostKeyChecking", "no");
			// creating connection
			session.connect();

			// creating channel in execution mod
			Channel channel = session.openChannel("exec");
			// sending command which runs bash-script in UploadPath directory
			((ChannelExec) channel).setCommand("bash " + UploadPath
					+ "bash.run");
			// taking input stream
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			// connecting channel
			channel.connect();
			// read buffer
			byte[] tmp = new byte[1024];

			// reading channel while server responses smth or until it does not
			// close connection
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					res.add(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					res.add("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			// closing connection
			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
