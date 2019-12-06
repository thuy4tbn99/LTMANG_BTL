package protocol;
public enum Message 
{
	// loai tin nhan
	RECEIVE_FILE, SEND_FILE, 
	MESSAGEForSERVER,
	MESSAGEForCLIENT,
	CLIENT_IP, 
	REDIRECT_CONNECTION; // server yeu cau client ket noi sang client moi de nhan file, chua ip client chuan bi dong vai tro server
}
