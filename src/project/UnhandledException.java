package project;

public class UnhandledException extends Exception{
	private static final long serialVersionUID = 1L;
	int errCode = 0;
	private String cause = null;
	private String source = null;
	public UnhandledException(int code, String details){
		errCode = code;
		source = details;
		switch(errCode){
		case 0x2: cause = "TypeNotMatchedException"; 	break;
		case 0x3: cause = "ValueOutOfRangeException"; 	break;
		case 0x4: cause = "ValueNotInEnumException"; 	break;
		//Error Class 100: IO Exceptions
		case 101: cause = "FileNotFoundException";		break;
		case 102: cause = "ImageIOException"; 			break;
		case 103: cause = "FileInputException";			break;
		case 104: cause = "FileOutputException";		break;
		case 100: cause = "OtherFileIOException";		break;
		//Error Class 200: Command Exceptions
		case 201: cause = "CommandSyntaxError"; 		break;
		}
	}
	public String getSource(){
		return source;
	}
	public String getcause(){
		return cause;
	}
	public void printStackTrace(){
		System.err.println("Error " + errCode + ": " + getcause());
		System.err.println("	" + getSource());
		System.err.println();
		System.err.print("	");
		super.printStackTrace();
		System.err.println();
		System.err.print("End Of Error Message");
	}
}