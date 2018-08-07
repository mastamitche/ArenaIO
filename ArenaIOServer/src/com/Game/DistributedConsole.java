package com.Game;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;


public class DistributedConsole extends Thread {

	static Scanner input = new Scanner(System.in);
	String line = "";
	String parameter = "";
	String[] word;
	serverDistributer server;
	Stack<Object> lastObjects = new Stack<Object>();
	
	Object curObj = serverDistributer.serverList.get(0);

	DistributedConsole(serverDistributer server){
		this.server = server;
	}
	
	@Override
	public void run(){
		lastObjects.push(curObj);
		String lineAll;
		String lastCommand = "";
		while ((lineAll = input.nextLine()) != null){
			if (lineAll.equals("!!")) lineAll = lastCommand;
			lastCommand = lineAll;
			for (String line:lineAll.split(";")){
				try{
					if (line.equals(""))
						continue;
					line = line.trim();
		
					word = line.split(" ");
					if (word.length > 1)
						parameter = line.replaceFirst("say ", "");
					else
						parameter = null;
		
					
					if (word[0].equalsIgnoreCase("say")){
						if (parameter != null){
							//server.notifyAll(parameter);
							System.out.println("Sent.");
						}else
							System.out.println("incorrect syntax. say <message>");
						
					}else if (word[0].equalsIgnoreCase("reset")){
						byte[] tosend = new byte[]{33};
						System.exit(1);
						
					}else if (word[0].equalsIgnoreCase("exit")){
						System.exit(123);
						
					}else if (word[0].equalsIgnoreCase("printQuadTree")){
						int tree = Integer.parseInt(word[1]);
						serverDistributer.serverList.get(0).trees[tree].printRundown();
	
					} else if (word[0].equalsIgnoreCase("ls")){
	
						//System.out.println("Methods in " + curObj.getClass().getSimpleName());
						
	
						for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
							System.out.println("\t\t\tMethods in " + c.getSimpleName());
							for (Method f : c.getDeclaredMethods()){
								f.setAccessible(true);
								System.out.println(f.getName());
							}
						}
	
						//System.out.println("Fields in " + curObj.getClass().getSimpleName());
						
						for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
							System.out.println("\t\t\tFields in " + c.getSimpleName());
							for (Field f : c.getDeclaredFields()){
								f.setAccessible(true);
								System.out.println(f.getName() + ": " + f.get(curObj));
							}
						}
						
					}else if (word[0].equalsIgnoreCase("cd")){
						
						if (word[1].equals("..")){
							if (lastObjects.size() > 1){
								lastObjects.pop();
								curObj = lastObjects.peek();
							}
							System.out.println("Set object to " + curObj.getClass().getSimpleName() + " " +  curObj);
						}else{
	
							Field nextField = null;
							outerloop:
							for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
								for (Field f : c.getDeclaredFields()){
									f.setAccessible(true);
									if (f.getName().equalsIgnoreCase(word[1])){
										nextField = f;
										break outerloop;
									}
								}
							}
							
							nextField.setAccessible(true);
							
							Object newObj = nextField.get(curObj);
							// Go through collections nicely
							if (newObj instanceof ArrayList){
								System.out.println("Index: ");
								String index = input.nextLine();
								int i = Integer.parseInt(index);
								if (i != -1)
									newObj = ((ArrayList)newObj).get(i);
							} else if (newObj instanceof Map){
								String index;
								if (word.length < 3){
									System.out.println("Index: ");
									index = input.nextLine();
								} else 
									index = word[2];
								
								
								System.out.println("What is the type of: " + index);
								String type = input.nextLine();
								Object key = StringToType(index, type);
								
								if (!(key instanceof Integer && (Integer) key == -1))
									newObj = ((Map)newObj).get(key);
							} else if (newObj instanceof Object[]){
								String index;
								if (word.length < 3){
									System.out.println("Index: ");
									index = input.nextLine();
								} else 
									index = word[2];
								int i = Integer.parseInt(index);
								if (i != -1)
									newObj = ((Object[])newObj)[i];
							}
							
							if (newObj != null){
								curObj = newObj;
								lastObjects.push(curObj);
								System.out.println("Set object to " + curObj.getClass().getSimpleName() + " " +  curObj);
							}else
								System.out.println("Null. Retaining old entry of " + curObj);
							
						}
						
						
					}else if (word[0].equalsIgnoreCase("set")){
						Field nextField = null;
	
						outerloop:
						for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
							for (Field f : c.getDeclaredFields()){
								f.setAccessible(true);
								if (f.getName().equalsIgnoreCase(word[1])){
									nextField = f;
									break outerloop;
								}
							}
						}
						if (nextField == null)
							System.out.println("No field: " + word[1]);
						else{
							nextField.setAccessible(true);
							nextField.set(curObj, StringToType(word[2], nextField.getType()));
						}
					}else if (word[0].equalsIgnoreCase("call")){
						Method usedMethod = null;
						outerloop:
						for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
							Method[] allMethods = c.getDeclaredMethods();
							for (Method m : allMethods){
								m.setAccessible(true);
								if (m.getName().equalsIgnoreCase(word[1])){
									usedMethod = m;
									break outerloop;
								}
							}
						}
						
						if (usedMethod == null) continue;
						Object returned = null;
						
						Class[] types = usedMethod.getParameterTypes();
						Object[] params = new Object[types.length];
						int i = 0;
						for (Class c : types){
							if (c.getSimpleName().equals("Object")){
								System.out.println("What is the type of: " + word[2+i]);
								String type = input.nextLine();
								params[i] = StringToType(word[2+i], type);
							}else{
								params[i] = StringToType(word[2+i], c);
							}
							i++;
						}
						
						returned = usedMethod.invoke(curObj, params);
						
						System.out.println("returned: " + returned);
						
						
					}else if (word[0].equalsIgnoreCase("cdcall")){
						Method usedMethod = null;
						outerloop:
						for (Class c = curObj.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
							Method[] allMethods = c.getDeclaredMethods();
							for (Method m : allMethods){
								m.setAccessible(true);
								if (m.getName().equalsIgnoreCase(word[1])){
									usedMethod = m;
									break outerloop;
								}
							}
						}
						
						if (usedMethod == null) continue;
						Object returned = null;
						
						Class[] types = usedMethod.getParameterTypes();
						Object[] params = new Object[types.length];
						int i = 0;
						for (Class c : types){
							if (c.getSimpleName().equals("Object")){
								System.out.println("What is the type of: " + word[2+i]);
								String type = input.nextLine();
								params[i] = StringToType(word[2+i], type);
							}else{
								params[i] = StringToType(word[2+i], c);
							}
							i++;
						}
						
						returned = usedMethod.invoke(curObj, params);
						
						if (returned != null){
							curObj = returned;
							lastObjects.push(curObj);
							System.out.println("Set object to " + curObj.getClass().getSimpleName() + " " +  curObj);
						}else
							System.out.println("Null. Retaining old entry of " + curObj);
						
						
					}else
						System.out.println("incorrect syntax. Commands: reset, exit");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public Object StringToType(String s, Class type){
		if(type.equals(int.class)) return Integer.parseInt(s);
		if(type.equals(Integer.class)) return Integer.parseInt(s);
		if(type.equals(float.class)) return Float.parseFloat(s);
		if(type.equals(Float.class)) return Float.parseFloat(s);
		if(type.equals(double.class))return Double.parseDouble(s);
		if(type.equals(Double.class))return Double.parseDouble(s);
		if(type.equals(String.class)) return s;
		if(type.equals(boolean.class))return s.equalsIgnoreCase("true");
		if(type.equals(Boolean.class))return s.equalsIgnoreCase("true");
		return null;
	}
	public Object StringToType(String s, String type){
		if(type.equalsIgnoreCase("int")) return Integer.parseInt(s);
		if(type.equalsIgnoreCase("float")) return Float.parseFloat(s);
		if(type.equalsIgnoreCase("double"))return Double.parseDouble(s);
		if(type.equalsIgnoreCase("string")) return s;
		if(type.equalsIgnoreCase("bool"))return s.equalsIgnoreCase("true");
		return null;
	}
	
	public void printArray(Object[] a){
		System.out.println("=================");
		for (Object o : a)
			System.out.println(o);
	}
	
}
