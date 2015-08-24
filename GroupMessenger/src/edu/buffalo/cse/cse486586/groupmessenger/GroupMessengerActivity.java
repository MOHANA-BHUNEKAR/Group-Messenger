package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();

    public static final int SERVER_PORT = 10000;
    static final String sequencer="11108";
    static final String portEmu1 = "11108";
    static final String portEmu2 = "11112";
    static final String portEmu3 = "11116";
    static final String portEmu4 = "11120";
    static final String portEmu5 = "11124";
    String EMPTY="EMPTY1";
    String GAT= "GAT1";
    String TAG1= "TAG1";
    static int buffercheck=0;
    String myPort;
    long msgid;
    String array1[]={portEmu1,portEmu2,portEmu3,portEmu4,portEmu5}; //,portEmu3
    int seqno = 0;
    int expectedSeq=0;
    int count=0;
    ArrayList<String> Buffer=new ArrayList<String>();
    int b=Buffer.size();
    ArrayList<String> MsgIdSeqno= new ArrayList<String>();
    HashMap<String,String> msgqueue = new HashMap<String,String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
       
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        msgid=Integer.parseInt(myPort)*100;
         
         Log.v(TAG,"In line 65  myport and msgid are: "+myPort+"  "+msgid);
         try {
           
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
           Log.v(TAG,"Server Socket created: and call new ServerTask().execute........");
           
           
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
           
           Log.v(TAG,"In line 74");
           
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }
       
   

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
       
        Log.v(TAG,"In line 98");
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    /*
                     * If the key is pressed (i.e., KeyEvent.ACTION_DOWN) and it is an enter key
                     * (i.e., KeyEvent.KEYCODE_ENTER), then we display the string. Then we create
                     * an AsyncTask that sends the string to the remote AVD.
                     */
                Log.i("listener for enter", "enter");
                    String msg = editText.getText().toString();
                    editText.setText(""); // This is one way to reset the input box.
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
                    return true;
                }
                return false;
            }
        });
        
        Log.v(TAG,"In Line 124");

        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        final Button send=(Button)findViewById(R.id.button4);
        View.OnClickListener myhandler=new View.OnClickListener() {
       
 
           
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
           
                String message=editText.getText().toString();
                editText.setText("");
                   new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message, myPort);

            }
        };
         send.setOnClickListener(myhandler);
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs in a total-causal order.
         */
     
         Log.v(TAG,"In Line 150");       
    }
   
    class ServerTask extends AsyncTask<ServerSocket, String, Void> {

       
   //-------------------------------------------------------------
        //    int sequenceNumber=1;
      //  int count=0;
       // String messageIdQ="";
      //  int temp=1;
       
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
        	
        	Log.v(TAG,"in doInBackground(ServerSocet....sockets");
            ServerSocket serverSocket = sockets[0];
            Socket clientSocket;
            InputStreamReader inputStreamReader;
           BufferedReader bufferedReader;
            String message ="";
            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             *
             */
          if(serverSocket != null)
            while (true) {
                try {
                   
                    clientSocket = serverSocket.accept(); 
                    inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader); //getting the client message
                   
                    message = bufferedReader.readLine(); 
                    Log.v(TAG,"---Message received and be send to publish progress is: "+message+" having port number: "+myPort);
                    if(message != null && !message.equals(""))
                    	this.publishProgress(message);
                    inputStreamReader.close();
                    clientSocket.close();
    
                } catch (IOException ex) {
                    System.out.println("Error in reading message");
                }
            }
           
            return null;
        }

        private Uri buildUri(String scheme, String authority) {
            // TODO Auto-generated method stub
           
             Uri.Builder uriBuilder = new Uri.Builder();
             uriBuilder.authority(authority);
             uriBuilder.scheme(scheme);
             return uriBuilder.build();
            //return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String message = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            //int count=0;
            String messageIdQ="";
            
            Log.v(TAG,"Entered into onProgressUpdate");
            Log.v(TAG,"Initial Buffer Size: "+b);
            Log.v(TAG," current buffer size for port number: "+myPort+" is "+Buffer.size());
            if(message != null && message.contains("##"))
            	Buffer.add(message);
            if(msgqueue.size() > 0)
            for(int i = 0; i <Buffer.size(); i++){
            		String s = Buffer.get(i);
            		String[] msgparts = s.split("##");
            		String msgiD = msgparts[1].trim();
            		String msg = msgparts[0].trim();
            		if(msgqueue.containsKey(msgiD)){
                		ContentResolver cr=getContentResolver();
                        Uri mUri=buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
                        ContentValues cv1=new ContentValues();
                        Log.v(GAT,"------------------------THE MESSAGE BEING INTO CONTENT PROVIDER: of port number: "+ myPort+" with mesage "+ msg+"--------->"+"with seq_no---> "+msgqueue.get(msgiD));
                       
                         cv1 = new ContentValues();
                            cv1.put("key",msgqueue.get(msgiD));
                            cv1.put("value", msg);
                            cr.insert(mUri, cv1);
                            expectedSeq++;
                            if(Integer.parseInt(msgqueue.get(msgiD)) == expectedSeq 
                            		&& Integer.parseInt(Buffer.get(Buffer.size()-1).split("##")[1].trim()) >= expectedSeq){
                            	expectedSeq++;
                            }                            		
                            remoteTextView.append(msgqueue.get(msgiD) + " : " + msg+"\n");
                            msgqueue.remove(msgiD);
                	}
            	}                   	
                if(message != null && message.contains("##"))
                {
                    //Buffer.add(message);                    
                    Log.v(TAG,"*********The message added to buffer  of port number are: "+message+"  "+myPort+" and buffer size: "+Buffer.size());
                   
                        String message1="";
                  
                  
                  
                            if(myPort.equals(sequencer) )
                            {
                  Log.v(TAG,"Port has been identified as sequencer and contains msg##msgid with my port number "+myPort+" sequence: "+sequencer);
                              message1=Buffer.get(count++);
                            Log.v(TAG,"Message retrieved from sequencer buffer is: "+message1);
                            String [] items = message1.split("##");
                            message1 = items[0];
                            messageIdQ = items[1];
                         
                  Log.v(TAG, "Now the msg and id from sequencer are: "+message1+" "+messageIdQ );        
                            messageIdQ=messageIdQ+"%^&"+seqno;
                  Log.v(TAG, "Now msgid and seqno to be multicasted are: "+messageIdQ);
                              seqno++;
                             
                             
                              //send to content provider
                              new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, messageIdQ, myPort);
                             
                             
                        }
                            else{
                            	Log.v(TAG,"Port is not identified as sequencer and its port number is: "+myPort);
                            }
                           
                    }
                        //break;
                //        }
                   
                    else
                    if(message.contains("%^&"))
                        {
                        Log.v(TAG,"Yes the messege contains msgid%^&seq before split"+message+" PORT_NUMBER: "+myPort);   
                        //    MsgIdSeqno.add(message);
                            String[] items = message.split("\\%\\^\\&");
                            String msgid1=items[0].trim();
                            int seqno1=Integer.parseInt(items[1]);
                           
                            Log.v(TAG, "After split msgid:--> "+msgid1+"  seqno--->"+seqno1);
                            String msgCP="";
                           
                           boolean foundMsg = false;
                            if(seqno1==expectedSeq || 1==1)
                            {
                               
                                Log.v(TAG,seqno1+"=="+expectedSeq+" Now check in buffer of "+myPort+" for message with msgid: "+msgid1);
                              // boolean flag=false;
                                for(int x1=0;x1<Buffer.size();x1++)
                                {
                                    String msgneww=Buffer.get(x1);
                                    String[] items1 = msgneww.split("##");
                                    //String msgCP=items1[0];
                                    String msgID=items1[1].trim();
                                   
                                    if(msgid1.equals(msgID))
                                    {
                                    foundMsg = true;
                                    Log.v(TAG,"THE MESSAGE BEING INTO CONTENT PROVIDER: "+items1[0]);
                                    msgCP=items1[0];
                                   
                                    Log.v(TAG,"THE MESSAGE BEING INTO CONTENT PROVIDER: "+items1[0]);
                                    
                                    String s11=String.valueOf(seqno1);         
                                    ContentResolver cr=getContentResolver();
                                    Uri mUri=buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
                                    ContentValues cv1=new ContentValues();
                                    Log.v(GAT,"------------------------THE MESSAGE BEING INTO CONTENT PROVIDER: of port number: "+ myPort+" with mesage "+ msgCP+"--------->"+"with seq_no---> "+s11);
                                   
                                     cv1 = new ContentValues();
                                        cv1.put("key",s11);
                                        cv1.put("value", msgCP);
                                        cr.insert(mUri, cv1);
                                        expectedSeq++;
                                        remoteTextView.append(s11 + " : " + msgCP+"\n");
                                        break;

                                    }
                                    if(foundMsg == false)
                                    	msgqueue.put(msgid1, items[1].trim());
                                }
                          /*      String s11=String.valueOf(seqno1);         
                                ContentResolver cr=getContentResolver();
                                Uri mUri=buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
                                ContentValues cv1=new ContentValues();
                                Log.v(GAT,"------------------------THE MESSAGE BEING INTO CONTENT PROVIDER: of port number: "+ myPort+" with mesage "+ msgCP+"--------->"+"with seq_no---> "+s11);
                               
                                 cv1 = new ContentValues();
                                    cv1.put("key",s11);
                                    cv1.put("value", msgCP);
                                    cr.insert(mUri, cv1);
                                    expectedSeq++;
				    remoteTextView.append(s11 + " : " + msgCP+"\n"); */
                                    //this.publishProgress(s11 + " : " + msgCP);                                    
                        //Again check in your buffer with the updated sequence number
                            
                                /*int newSeq=expectedSeq;
                            
                                  //  int zount=0;                               
                                    while(true)
                                    {
                                       
                                       
                                    boolean seqMatcher = false;   
                                    for(int x2=0;x2<MsgIdSeqno.size();x2++)
                                    {
                                       
                                        // extract msgid and seq no of each element
                                       remoteTextView.append("Entered into an infinite loop");
                                        String MsSn=MsgIdSeqno.get(x2);
                                        String [] items3 = MsSn.split("\\%\\^\\&");

                                        int seqno11= Integer.parseInt(items3[1].trim());
                                        String msgid11=items3[0].trim();
                                        
                                    Log.v(TAG,"Extracted MessageId and Sequence-Number"+ msgid11+ "  "  +seqno11);    
                                        if(seqno11==expectedSeq)
                                        {
                                        	seqMatcher = true;
                                            //extract message using msgid from buffer
                                            for(int x1=0;x1<Buffer.size();x1++)
                                            {
                                                String msgneww=Buffer.get(x1);
                                                String[] items1 = msgneww.split("##");
                                             msgCP=items1[0];
                                                String msgID=items1[1];
                                               
                                                if(msgid11.equals(msgID))
                                                {
                                                foundMsg = true;
                                                msgCP=items1[0];   
                                                String s12=String.valueOf(seqno11);  
                                                ContentResolver cr=getContentResolver();
                                                Uri mUri=buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
                                                ContentValues cv1=new ContentValues();
                                                Log.v(GAT,"Message inserted in CP and its sequence number are: "+ msgCP+ " "+s12);
                                                cv1 = new ContentValues();
                                                 cv1.put("key",s12);
                                                 cv1.put("value", msgCP);
                                                 cr.insert(mUri, cv1);
                                                expectedSeq++;
                                               	  remoteTextView.append(s12 + " : " + msgCP);
						                          break;
                                               
                                                }
                                               
                                               
                                            }   
                                           
                                           
                                           
                                        }
                                 }
                                       
                                       
                                 if(!seqMatcher)
                                 {
                                	 //remoteTextView.append("exiting from infinite loop");
                                     break;
                                 }                                                                       
                              }*/                           
                           }
                            else
                            {
                                if(foundMsg == false)
                                	msgqueue.put(msgid1, items[1].trim());
                                MsgIdSeqno.add(message);
                                Log.v(TAG,"Expected sequence number did n't match with the received sequence number");
                                Log.v(TAG,"Expected sequence number :"+expectedSeq+" but received "+seqno1);
                                Log.v(TAG,"---MessageIdSeqno--  "+message);

                            }
                           
                           
                           
                  
                        }                                                              
		
                   Log.v(TAG,"Returing from publish progress with port number: "+myPort);
                   
            return;
        }
    }
   
    class ClientTask extends AsyncTask<String, Void, Void> {
        private PrintWriter  printwriter;
                @Override
                protected Void doInBackground(String... msgs) {
                    try {
                       
                       
                   
           for(int i=0;i<array1.length;i++)
           {
               Log.v(TAG, "Inside for loop of client task: "+array1[i]);
                           String remotePort=array1[i];
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remotePort));
                        String msgToSend;
                        if(msgs[0].contains("%^&"))
                        {
                            msgToSend=msgs[0];
                            Log.v(TAG, "the messege contains msgid and seqno "+msgToSend);

                        }
                        else
                        {
                        	Log.v(TAG,"Before buffer check: "+buffercheck);
                        	buffercheck++;
                        	Log.v(TAG,"After buffer check: "+buffercheck);

                        msgToSend = msgs[0]+"##"+msgid;
                         Log.v(TAG, "the messege contains msg and msgid "+msgToSend);
                      
                        }
                        printwriter = new PrintWriter(socket.getOutputStream(),true);
                        Log.v(TAG,"The message i'm sending is: "+ msgToSend);
                        printwriter.write(msgToSend);
                        Log.v(TAG," In Line 433");
                        printwriter.flush();
                        Log.v(TAG," In Line 435");
                        printwriter.close();
                        Log.v(TAG," In Line 437");

                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */
                        socket.close();
                       
           }
          
          
                    } catch (UnknownHostException e) {
                        Log.e(TAG, "ClientTask UnknownHostException");
                    } catch (IOException e) {
                        Log.e(TAG, "ClientTask socket IOException");
                    }
                    if(!msgs[0].contains("%^&"))
                    {
                    	Log.v(TAG,"Came out of for loop Before incrementing msgid: "+msgs[0]+" with port number: "+myPort);
                    	msgid++;
                    	Log.v(TAG,"After incrementing msg id: "+ msgid+" with port number: "+myPort);
                    }
                    else
                    {
                    	Log.v(TAG,"Came out of for loop but contains %^&:  "+msgs[0]+" with port number: "+myPort);
                    }
                    return null;
                }
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
