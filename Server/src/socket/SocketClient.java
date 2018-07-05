package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.Clothes;
import database.ClothesInfo;
import database.UserInfo;
import database.Weather;
import database.WeatherState;
//�ͻ���
public class SocketClient {
	
	private Socket client = null;
	private static OutputStream outputStream=null;
	private static InputStream inputStream=null;
	private String host;
	private int post;
	
	public SocketClient(String host,int post) 
	{
		this.host=host;
		this.post=post;
	}
	
	public void Start() throws IOException
	{
		//���������������
		client=new Socket(host,post);
		//�������Ӻ��õ����롢�����
		outputStream=client.getOutputStream();
		inputStream=client.getInputStream();
	}
	
	public void Close() throws IOException
	{
		outputStream.close();
		inputStream.close();
		client.close();
	}
	
	
	private void sendMessage(String message) throws IOException 
	{
		Send.sendMessage(message, outputStream);
	}
	
	private void sendPicture(String addr) throws IOException
	{
		Send.sendPicture(addr, outputStream);
	}
	
	private String receiveMessage() throws IOException
	{
		String result=Receive.receiveMessage(inputStream);
		return result;
	}
	
	private void receivePicture(String addr) throws IOException
	{
		Receive.receivePicture(inputStream, addr);
	}
	
	//�����ⲿ�����û�ע���ǵ���
	//�ͻ��˴������������������������ݸ����ݿ⣬���ݿ���룬���ж��Ƿ��ظ�
	//����ظ�������-1����ע��ʧ��
	//���ظ������ػ�ȡ��id�ţ���ע��ɹ�
	public int sendUserInfo_register(String name,String password) throws IOException
	{
		String message="0,"+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String[] result=receiveMessage().split(",");	//���ص���Ϣ
		if(result[0].equals("1"))
		{
			System.out.println("�ͻ���ע��ɹ������õ�id��Ϊ��"+result[1]);
			//���ܻ�Ҫ�������������ͻ����²�
			Close();
			return Integer.parseInt(result[1]);
		}
		else
		{
			System.out.println("�ͻ���ע��ʧ�ܣ�");
			Close();
			return -1;
		}
	}
	
	//�����û���½
	public boolean sendUserInfo_login(String name,String password) throws IOException
	{
		String message="1,"+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String[] result=receiveMessage().split(",");	//���ص���Ϣ
		if(result[0].equals("1"))
		{
			System.out.println("�ͻ��˵�½�ɹ������õ�id��Ϊ��"+result[1]);
			//������ҲҪ���ؿͻ����²�
			//��Ҫ���صĲ�ֹid������������Ϣ
			Close();
			return true;
		}
		else
		{
			System.out.println("�ͻ��˵�½ʧ�ܣ�");
			Close();
			return false;
		}
	}
	
	//�����û�ע��
	//���庬�����ƺ����Ĳ���
	//һ����������ɾ�����û�����Ϣ��ͬʱ�ͻ��˽�������Ϣɾ��
	//����ֻ�Ǽ򵥵ص��롢�ǳ�������QQ
	//����ʵ�ֵ���һ
	public boolean sendUserInfo_logoff(Integer id,String name,String password) throws IOException
	{
		String message="2,"+id+","+name+","+password;
		Start();
		sendMessage(message);	//�ڴ��õ����ص���Ϣ
		String result=receiveMessage();	//������Ϣ
		if(result.equals("1"))
		{
			System.out.println("�ͻ���ע���ɹ���");
			Close();
			return true;
		}
		else
		{
			System.out.println("�ͻ���ע��ʧ�ܣ�");
			Close();
			return false;
		}
	}
	
	//���͵����·���������Ϣ
	public void sendClothInfo_text(Integer user_id,Clothes cloth) throws IOException
	{
		String message="3,"+user_id+","+cloth.getClothesId()+","+cloth.getClothesClass()+","+cloth.getClothesType()+","+cloth.getClothesColor()+","+cloth.getClothesDirtyDegree();
		Start();
		sendMessage(message);					//���͵���һ���·�����Ϣ
		/*
		String result=receiveMessage();		//�õ�����ϢӦ�����Ƽ���Ϣ���������ǿ��ж�Ӧ���߼�
		System.out.println("�ͻ����Ƽ���ϢΪ��"+result);
		*/
		Close();
	}
	
	//���͵����·���ͼƬ��Ϣ
	//addr��ʾͼƬ�ڱ��صĵ�ַ
	//����user_id��cloth_id������ڷ������˵�ͼƬ�洢��
	public void sendClothInfo_picture(String addr,Integer user_id,Integer cloth_id) throws IOException
	{
		String message="5,"+user_id+","+cloth_id;
		Start();
		sendMessage(message);	//"���ݱ�ʾ6���������˾ݴ��жϳ������ڷ���ͼƬ��"
		sendPicture(addr);
		System.out.println("�ͻ���ģ�鷢��ͼƬ��Ϣ���!");
		Close();
	}
	
	public void sendHistoryInfo(Integer user_id,Weather weather,List<Integer> clothesIdList) throws IOException
	{
		String message="4,"+user_id+","+weather.getState().ordinal()+","+weather.getUpperTemperature()+","+weather.getLowerTemperature()+","+weather.getUpperHumidity()+","+weather.getLowerHumidity()+","+weather.getUpperWindForce()+","+weather.getLowerWindForce()+",";
		for(int i=0;i<clothesIdList.size();i++)
		{
			message+=clothesIdList.get(i);
			if(i!=(clothesIdList.size()-1))
			{
				message+=",";
			}
		}
		Start();
		sendMessage(message);
		System.out.println("�ͻ��˷���һ�����ʷ��Ϣ�ɹ�!");
		Close();
	}
	
	public void sendTodayInfo(Integer user_id,Weather weather,Map<String, Integer> params,int attr) throws IOException
	{
		String message="6,"+user_id+","+weather.getState().ordinal()+","+weather.getUpperTemperature()+","+weather.getLowerTemperature()+","+weather.getUpperHumidity()+","+weather.getLowerHumidity()+","+weather.getUpperWindForce()+","+weather.getLowerTemperature()+","+attr;
		//����Map 
		for (Entry<String, Integer> entry : params.entrySet()) 
		{
			if(entry.getKey().equals("����"))
			{
				message+=",1,"+entry.getValue();
			}
			else if(entry.getKey().equals("����"))
			{
				message+=",2,"+entry.getValue();
			}
			else if(entry.getKey().equals("��װ"))
			{
				message+=",3,"+entry.getValue();
			}
			else if(entry.getKey().equals("Ь��"))
			{
				message+=",4,"+entry.getValue();
			}
		}
		Start();
		sendMessage(message);
		System.out.println("�ͻ��˷��ͽ������ʷ��Ϣ�ɹ�!");
		String result=receiveMessage();		//�õ�����ϢӦ�����Ƽ���Ϣ���������ǿ��ж�Ӧ���߼�
		System.out.println("�ͻ��˵õ����Ƽ���ϢΪ��"+result);
		Close();
	}
	public static void main(String args[]) throws Exception
	{
		//���ӵķ�����IP��ַ�Ͷ˿�
		String host="127.0.0.1";
		int post=55533;
		SocketClient socketclient=new SocketClient(host,post);
		//��ʵ����Ŀ�У����Ͻ������Ӳ���Ӧ����MainActivity�оͽ��е��ã�ע��Ӧ��Server��������Ȼ�����Client����
		
		//Ŀǰ���ҹ涨���͵�����ֻ�����࣬һ�����û��������룬һ�����·���Ϣ
		//ÿһ����Ϣ�в�ͬԪ�ض����ö��ŷָ����������Ǳ�ڵ��û������������Ϣ������ֶ���������Ԫ��
		//���ڶ�Ԫ�ش���ʱ��ͨ���ָ����ʵ�ֲ�ͬԪ�صĻ�ȡ
		
		//����ע��
		String name="lgy";
		String password="1234567";
		//ע��ɹ����ں�������һ���û�id
		int user_id=socketclient.sendUserInfo_register(name, password);
		if(user_id==-1)
		{
			;//�ڰ�׿�ǿ����toastһ��,����ע��ʧ��
			return;
		}
	
		//UserInfo(Integer u_id, String u_name, String u_password, ClothesInfo u_clothesInfo)
		UserInfo userinfo=new UserInfo(user_id,name,password,null);
			
		Clothes cloth1=new Clothes(1,1,0,12,0);
        Clothes cloth2=new Clothes(2,1,0,7,0);
        Clothes cloth3=new Clothes(3,1,0,4,0);
        Clothes cloth4=new Clothes(4,0,2,13,0);
        Clothes cloth5=new Clothes(5,0,2,13,0);
        Clothes cloth6=new Clothes(6,0,0,7,0);
        Clothes cloth7=new Clothes(7,2,1,6,0);
        Clothes cloth8=new Clothes(8,2,1,4,0);
        Clothes cloth9=new Clothes(9,2,1,11,0);
        Clothes cloth10=new Clothes(10,3,0,7,0);
        Clothes cloth11=new Clothes(11,3,0,0,0);
        Clothes cloth12=new Clothes(12,3,0,4,0);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth1);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth2);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth3);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth4);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth5);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth6);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth7);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth8);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth9);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth10);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth11);
        socketclient.sendClothInfo_text(userinfo.getId(), cloth12);
       
        
		List<Integer> clothsIdList=new ArrayList<>();
		clothsIdList.add(cloth4.getClothesId());
		clothsIdList.add(cloth1.getClothesId());
		clothsIdList.add(cloth7.getClothesId());
		clothsIdList.add(cloth10.getClothesId());
		List<Integer> val1 = Arrays.asList(2,3,4,5,6,7,8);
        Weather weather1 = new Weather(val1);
		socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
		
		List<Integer> val2 = Arrays.asList(2,3,4,5,6,7,8);
        Weather weather2 = new Weather(val1);
        Map<String, Integer> params=new HashMap<>();
        params.put("����", 1);
        params.put("Ь��", 10);
        params.put("��װ", 7);
        
		socketclient.sendTodayInfo(userinfo.getId(), weather2, params, 1);		//��ѡһ������
		
		

	}
}
