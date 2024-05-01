package com.yeaayy.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Log implements Thread.UncaughtExceptionHandler {
	private static boolean exitAfterError;
	private static FileOutputStream out;

	public static void init(File path){
		Thread.setDefaultUncaughtExceptionHandler(new Log());
		try{
			out=new FileOutputStream(new File(path, "log.txt"), false);
		}catch(IOException e){}
	}

	public static void print(Throwable e){
		int i=0;
		do{
			println(e.toString());
			for(StackTraceElement ste:e.getStackTrace()){
				println("at "+ste.toString());
			}
		}while((e=e.getCause())!=null&&i++<5);
		if(exitAfterError)System.exit(0);
	}

	public static void printStackTrace(StackTraceElement[]stes){
		for(StackTraceElement ste:stes){
			println(ste.toString());
		}
	}

	public static void printf(String fmt,Object...o){
		print(String.format(fmt,o));
	}

	public static void printlnf(String fmt,Object...o){
		println(String.format(fmt,o));
	}

	public static void print(Object o){
		print(String.valueOf(o));
	}

	public static void println(Object o){
		print(String.valueOf(o)+"\n");
	}

	public static void print(String str){
		try{
			out.write(str.getBytes());
		}catch(IOException e){}
	}

	public static void println(String str){
		print(str+"\n");
	}

	public static void exitAfterError(boolean b){
		exitAfterError=b;
	}

	@Override
	public void uncaughtException(Thread thread,Throwable throwable){
		print(throwable);
	}
}
