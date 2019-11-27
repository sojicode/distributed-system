/*--------------------------------------------------------------------------------------

1. Name / Date: Sojeong Yang / May 18 2019

2. Java(TM) SE Runtime Environment (build 1.8.0_211-b12)

3. command-line examples:

> javac Blockchain.java

4. Other Instructions :

In separate shell terminal:
> java Blockchain 0
> java Blockchain 1
> java Blockchain 2

5. Files needed for running the program.

 a. Blockchain.java
 b. BlockInput0.txt
 c. BlockInput1.txt
 d. BlockInput2.txt

6. Notes:

I used my Mac laptop for this project.

These are console commands examples:

[C] -> Credit
[V] -> Verify
[V hash] -> Verify/invalid
[L] -> List
[R filename] -> Read a file (ex. R BlockInput0.txt)
---------------------------------------------------------------------------------------*/

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.security.spec.*;
import java.security.*;


@XmlRootElement
class BlockRecord {
	//Block's information field from all the blocks in the chain.
	String SHA256String;
	String SignedSHA256;
	String BlockID;
	String SignedBlockID;
	String VerificationProcessID;
	String CreatingProcess;
	String Fn;
	String Ln;
	String SSNum;
	String ACreatedTime;
	String AVerifiedTime;
	String ADataHash;
	String ASignedDataHash;
	String ARandomSeedString;
	String ABlockNum;
	String DOB;
	String Diag;
	String Treat;
	String Rx;

	//this code frame is from given sample code in our class, A, F, G inside function call for sorting by XML tool 
	//A -> for header
	public String getASHA256String() {return SHA256String;} //SHA256 String
	@XmlElement
	public void setASHA256String(String SH){SHA256String = SH;}

	public String getASignedSHA256() {return SignedSHA256;} //Signed SHA256
	@XmlElement
	public void setASignedSHA256(String SH){SignedSHA256 = SH;}

	public String getABlockNum() {return ABlockNum;} //number of block
	@XmlElement
	public void setABlockNum(String BN){ABlockNum = BN;}

	public String getACreatingProcess() {return CreatingProcess;} //Creating process
	@XmlElement
	public void setACreatingProcess(String CP){CreatingProcess = CP;}

	public String getAVerificationProcessID() {return VerificationProcessID;} //verify process identification
	@XmlElement
	public void setAVerificationProcessID(String VID){VerificationProcessID = VID;}

	public String getABlockID() {return BlockID;} //block identification
	@XmlElement
	public void setABlockID(String BID){BlockID = BID;}

	public String getASignedBlockID() {return SignedBlockID;} //Signed Block Identification
	@XmlElement
	public void setASignedBlockID(String SBID){SignedBlockID = SBID;}

	public String getACreatedTime() {return ACreatedTime;} //create time stamp
	@XmlElement
	public void setACreatedTime(String T) {ACreatedTime = T;}

	public String getAVerifiedTime() {return AVerifiedTime;} //verified time stamp
	@XmlElement
	public void setAVerifiedTime(String T) {AVerifiedTime = T;}

	public String getADataHash() {return ADataHash;} //Data hash
	@XmlElement
	public void setADataHash(String D) {ADataHash = D;}

	public String getASignedDataHash() {return ASignedDataHash;} //signed data hash
	@XmlElement
	public void setASignedDataHash(String D) {ASignedDataHash = D;}

	public String getARandomSeedString() {return ARandomSeedString;} //randomseed string
	@XmlElement
	public void setARandomSeedString(String RSS) {ARandomSeedString = RSS;}

	//F -> for identification
	public String getFSSNum() {return SSNum;} //Social security number
	@XmlElement
	public void setFSSNum(String SS){SSNum = SS;}

	public String getFFName() {return Fn;} //first name
	@XmlElement
	public void setFFName(String FN){Fn = FN;}

	public String getFLName() {return Ln;} //last name
	@XmlElement
	public void setFLName(String LN){Ln = LN;}

	public String getFDOB() {return DOB;} //date of birth
	@XmlElement
	public void setFDOB(String DOB){this.DOB = DOB;}

	//G -> for medical 
	public String getGDiag() {return Diag;} //diagnosis
	@XmlElement
	public void setGDiag(String D){Diag = D;}

	public String getGTreat() {return Treat;} //treatment
	@XmlElement
	public void setGTreat(String D){Treat = D;}

	public String getGRx() {return Rx;} //prescription
	@XmlElement
	public void setGRx(String D){Rx = D;}

	public boolean findSameBlock(BlockRecord brecord) {
		//compare to the blocks and find it's same record, return true 
		return (SHA256String.equals(brecord.SHA256String) && SignedSHA256.equals(brecord.SignedSHA256)
				&& BlockID.equals(brecord.BlockID) && SignedBlockID.equals(brecord.SignedBlockID)
				&& VerificationProcessID.equals(brecord.VerificationProcessID) && CreatingProcess.equals(brecord.CreatingProcess)
				&& Fn.equals(brecord.Fn) && Ln.equals(brecord.Ln) && SSNum.equals(brecord.SSNum)
				&& DOB.equals(brecord.ACreatedTime) && DOB.equals(brecord.AVerifiedTime) && DOB.equals(brecord.ADataHash)
				&& DOB.equals(brecord.ASignedDataHash) && DOB.equals(brecord.ARandomSeedString) && DOB.equals(brecord.ABlockNum) 
				&& DOB.equals(brecord.DOB) && DOB.equals(brecord.Diag) && DOB.equals(brecord.Treat) && DOB.equals(brecord.Rx));
	}
}

class BChainServer implements Runnable {

	public boolean On = true;
	public DatagramSocket socket = null; //initial datagram-socket is empty
	public UnverifiedBServer unverifiedBlockServer;
	public LinkedList<BlockRecord> bChain; //using linkedList for blockRecord and initialize to blockChain
	public ArrayList<String> bChainBlockIds; 
	public int pid; //process id

	public BChainServer(int port, UnverifiedBServer unverifiedBServer, int processId) throws SocketException {
		//SocketException for socket cannot opened or bind to the specific port.
		//using DatagramSocket, different packets could arrive without ordering
		socket = new DatagramSocket(port); //to receive broadcasts on UDP with the port  
		unverifiedBlockServer = unverifiedBServer;
		bChain = new LinkedList<BlockRecord>();
		bChainBlockIds = new ArrayList<String>();
		pid = processId;
	}

	public void run() {

		byte[] buffer = new byte[102500]; //create an byte array to store packets from the socket
		DatagramPacket callPacket = new DatagramPacket(buffer, buffer.length);
		while (On) { //get all packets from the socket
			try {
				socket.receive(callPacket); //receive a datagram-packet from the socket
			} catch (IOException ioe) { System.out.println("This is IOException"); }
			String data = new String(buffer, 0, callPacket.getLength());
			try {
				this.updateBChain(data); //update a new block
			} catch (JAXBException je) { System.out.println("This is JAXBException"); }
			if (pid == 0) { //if process id is 0, write the info to BlockchainLedger
				try {
					FileWriter fwr = new FileWriter(new File("BlockchainLedger.xml"));//file object(BlockchainLedger.xml)
					fwr.write(data); //writing new portion of block chain string 
					fwr.flush(); //rush the stream
					fwr.close(); //close the stream
				} catch (IOException ioe) { System.out.println("This is IOException"); }
			}
		}
	}

	public void updateBChain(String newBChainString) throws JAXBException {

		String XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";//given sample code for XMLHeader from professor
		String wipeXml = newBChainString.replace(XMLHeader,"").replace("<BlockLedger>","").replace("</BlockLedger>","");
		LinkedList<BlockRecord> newBChain = new LinkedList<BlockRecord>(); //assign to new block chain by LinkedList
		ArrayList<String> newBChainBlockIds = new ArrayList<String>(); //assign to new blockIds by ArrayList
		BlockRecord block;
		for (String b: wipeXml.split("\n\n")) {//split the block
			StringReader sr = new StringReader(XMLHeader + b); //read the source(XMLHeader + b)
			//this is tools for managing the XML binding, given sample code from professor
			JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class);////this is tool for managing the XML binding 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); //object that convert a java into XML data
			BlockRecord blockRecord = (BlockRecord) jaxbUnmarshaller.unmarshal(sr); //unmarshal XML to blockRecod type
			block = blockRecord; //assign a new BlockRecord to block
			if (block != null) { //if block is not null, 
				newBChain.add(block); //add that block to new blockChain
				newBChainBlockIds.add(block.getABlockID()); //add that blockId to new blockChain
			}
		}       
		if (bChain.size() == newBChain.size()) { //if new block chain size is same as existed one
			for (int n = 1; n < bChain.size(); n++) {//check through the blockChain 
				if(!bChain.get(n).findSameBlock(newBChain.get(n))) { break; } //compare to each their field
			}
		} else if (bChain.size() < newBChain.size()) {//if new block chain size isn't same as existed one
			bChain = newBChain; //the blockChain is new BlockChain
			bChainBlockIds = newBChainBlockIds; //the blockChainIds is new BlockChainIds
			unverifiedBlockServer.updateBChain(bChain, bChainBlockIds); //update new block to blockChain
		}
	}

	public void listBChain() {
		//return from the command line 'L', it displays the list of block chain
		for (int n = bChain.size()-1; n > 0; n--) { //iterate through block chain 
			BlockRecord br = bChain.get(n); //get the block chain record
			System.out.println(br.getABlockNum() + ") " + br.getAVerifiedTime() + " " + br.getFFName()+ " " + br.getFLName() 
			+ " " + br.getFDOB() + " " + br.getFSSNum() + " " + br.getGDiag() + " " + br.getGTreat() + " " + br.getGRx());
			//print-> block number)time/Name/DateOfBirth/SSNum/Diagnosis/Treatment/Prescription
		}
	}

	public void verifyBChain(int num) throws Exception {
		//run from the 'V' command in the terminal, verify the  block chain
		for (int n = 1; n < bChain.size(); n++) { //to verify, iterate through the blockChain
			BlockRecord preB = bChain.get(n-1); //previous block 
			BlockRecord currB = bChain.get(n); //current block

			// to compare that current block's header is same as what we make now and verify it
			BlockRecord dup = new BlockRecord(); //assign dup object 
			dup.setABlockNum(currB.getABlockNum()); dup.setABlockID(currB.getABlockID());
			dup.setACreatingProcess(currB.getACreatingProcess()); dup.setACreatedTime(currB.getACreatedTime());
			dup.setADataHash(currB.getADataHash()); dup.setARandomSeedString(currB.getARandomSeedString());
			dup.setASignedBlockID(currB.getASignedBlockID()); dup.setASignedDataHash(currB.getASignedDataHash());
			dup.setFDOB(currB.getFDOB()); dup.setFFName(currB.getFFName()); dup.setFLName(currB.getFLName());
			dup.setFSSNum(currB.getFSSNum()); dup.setGDiag(currB.getGDiag()); dup.setGTreat(currB.getGTreat());
			dup.setGRx(currB.getGRx());			

			//This code was given sample code from professor
			StringWriter strwt = new StringWriter(); //assign StringWriter strwt object
			JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class);//this is tool for managing the XML binding 
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();//after change the object to XML, format to string 
			//It will format with line breaks and indentation for the resulting XML data 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//format with line breaks and indentation
			jaxbMarshaller.marshal(dup, strwt); //XML send to StringWriter s
			MessageDigest md = MessageDigest.getInstance("SHA-256"); //make SHA-256 instance through MessageDigest class
			String blockXML = strwt.toString(); //assign String blockXML and put s into it 
			md.update((blockXML + preB.getASHA256String()).getBytes()); //update SHA256 with array of bytes(blockXML+previous block)
			byte[] byteData = md.digest(); //byteData array of bytes with hash value.

			//This is given sample code from professor, it changes the format from byte[] to hex. 
			StringBuffer strb = new StringBuffer(); //assign strb object 
			for (int s = 0; s < byteData.length; s++) { //iterate through byteData 
				strb.append(Integer.toString((byteData[s] & 0xff) + 0x100, 16).substring(1)); //append it with hex format
			}
			String SHA256String = strb.toString();
			if (!currB.getASHA256String().equals(SHA256String) || num == 1) {//current block's SHA256String is not same or num equal to 1
				System.out.println("Block "+currB.getABlockNum()+" invalid: sha256 hash does not match.");//print out not match
			}
			num = 0; //assign num back to 0
		} //print out the result
		System.out.println("Blocks 1-"+ (bChain.size()-1) +" in the blockchain have been verified.");
	}
	/* To display the credit information from the C commands in the terminal */
	public void getCredit() throws Exception {
		int P0 = 0; //initial credit for process 0
		int P1 = 0; //initial credit for process 1
		int P2 = 0; //initial credit for process 2
		for (int i = 1; i < bChain.size(); i++) {
			BlockRecord currBlock = bChain.get(i);//get the current block info
			String verify = currBlock.getAVerificationProcessID(); //
			if (verify.equals("Process0")) P0++; //if it's equals process0, add credit to process0
			if (verify.equals("Process1")) P1++; //if it's equals process1, add credit to process1
			if (verify.equals("Process2")) P2++; //if it's equals process2, add credit to process2
		} //print out the result
		System.out.println("Verification credit: P0="+P0+", P1="+P1+", P2="+P2); //print out the credit info     
	}
}

class BlockVerifyWorker extends Thread {

	public boolean ON = true; //assign boolean 'On' to true
	public LinkedList<BlockRecord> bChain; //blockChain put into LinkedList
	public ArrayList<String> bChainBlockIds; //blockChainBlockId put into ArrayList
	private Queue<BlockRecord> unverifiedBs; //assign unverified Blocks to put the block record into queue
	private DatagramSocket socket; //assign UDP socket 
	private int pid;
	public HashMap<String, PublicKey> publicKeys; //assign publicKeys by HashMap 
	public KeyPair keyPair; //assign keyPair to hold a public key & a private key

	public BlockVerifyWorker(DatagramSocket socket, int pid, KeyPair keyPair) { //constructor
		this.socket = socket; //this socket equal to parameter socket
		this.pid = pid; ////this pid equal to parameter pid
		this.keyPair = keyPair; ////this keyPair equal to parameter keyPair
		unverifiedBs = new LinkedList<BlockRecord>(); //create new unverifiedBs object
		bChain = new LinkedList<BlockRecord>(); //create new blockChain object

		//generating a unique blockID, this code frame for given sample code from professor
		String uu = UUID.randomUUID().toString(); //random UUID to string format
		BlockRecord blockZero = new BlockRecord(); //assign blockZero
		blockZero.setABlockID(uu); //give block id to UUID
		blockZero.setABlockNum("0"); //set the initial block number to 0
		blockZero.setFSSNum("321-54-9876"); //set the initial block SSNum
		blockZero.setFFName("Beatrix"); //set the initial block First name
		blockZero.setFLName("Kiddo"); //set the initial block Last name
		blockZero.setGDiag("ObessiveRevenge"); //set the initial block diagnosis
		blockZero.setGTreat("Meditation"); //set the initial block treatment
		blockZero.setGRx("IceCream"); //set the initial block prescription
		bChain.add(blockZero); //add this initial block to the block chain
		bChainBlockIds = new ArrayList<String>(); //put this blockId to the array list
		bChainBlockIds.add(uu); //add this unique id to blockChainId
		publicKeys = new HashMap<String,PublicKey>(); //put string and public-key into hashMap
	}

	public void addBlock(String block) throws JAXBException {//this is a function to add block
		BlockRecord newBlock; //assign to newBlock
		StringReader rd = new StringReader(block); //read from block
		JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class);//this is tool for managing the XML binding, given sample code
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller(); //change from XML to java object 
		BlockRecord blockRecord = (BlockRecord)jaxbUnmarshaller.unmarshal(rd); //wrap to BlockRecord type
		newBlock = blockRecord; //now newBlock is blockRecord
		if (newBlock != null) { //if new block is null
			unverifiedBs.add(newBlock); //this newBlock adds to queue unverifiedBs
		}
	}

	public void updateBChain(LinkedList<BlockRecord> newBlockChain, ArrayList<String> newBlockChainBlockIds) {
		//To set for the new block chain to update
		bChain = newBlockChain; ////update new BlockChain
		bChainBlockIds = newBlockChainBlockIds; //update new BlockChain BlockId
	}

	public void run() {

		while (ON) {
			if (!unverifiedBs.isEmpty()) { //if unverified blocks' queue is not empty
				BlockRecord block = unverifiedBs.remove(); //remove queue of unverified blocks' and assign to block
				PublicKey publicKey = publicKeys.get(block.getACreatingProcess()); //get the public key
				byte[] decodedSignedBlockID = Base64.getDecoder().decode(block.getASignedBlockID());//get the byte array of decoded-signed-BlockID
				boolean verifiedBlockID; //assign to verifiedBlockID with boolean value
				try {//verify the block using verifySig function
					verifiedBlockID = verifySig(block.getABlockID().getBytes(), publicKey, decodedSignedBlockID);
				} catch (Exception ex) {
					verifiedBlockID = false; //catch the Exception, change the boolean value
				}
				if (!verifiedBlockID) //if verifiedBlockID not true,
					continue; //continue to while loop
				BlockRecord lastBlock = bChain.getLast(); //get the last element in blockChain
				Integer blockID = Integer.valueOf(lastBlock.getABlockNum())+1; //add 1 the last block number by Integer type
				block.setABlockNum(blockID.toString()); //set the number to blockNum
				String previousSHA256 = lastBlock.getASHA256String(); //assign last block SHA256String to previousSHA256
				String blockId = block.getABlockID(); //get the blockId

				try { //this is tool for managing the XML binding, given sample code from professor 
					JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class); //this is tool for managing the XML binding 
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); //after change the object to XML, format to string 
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//format with line breaks and indentation

					byte[] bt = new byte[256]; //space for SHA's hash size, 256 
					Random rand = new Random(); //assign rand by Random class
					while (true) { //while it is true, 
						rand.nextBytes(bt); //makes random bytes and put them into a byte array
						StringWriter strw = new StringWriter(); //assign strw by StringWriter class
						block.setARandomSeedString(Base64.getEncoder().encodeToString(bt)); //convert byte to string
						jaxbMarshaller.marshal(block, strw);//XML send to StringWriter strw
						String blockXML = strw.toString(); //assign blockXML with strw string

						//make SHA-256 instance through MessageDigest class
						MessageDigest md = MessageDigest.getInstance("SHA-256");
						md.update((blockXML + previousSHA256).getBytes()); //update SHA-256
						byte[] byteData = md.digest();//byteData array of bytes with hash value.

						//convert the byteDate to binary format
						StringBuffer strbr = new StringBuffer();
						for (int i = 0; i < 4; i++) { // 4*8, SHA-256 is 32 bits 
							strbr.append(Integer.toBinaryString((byteData[i] & 0xFF) + 0x100).substring(1));//append it with binary format
						}
						Long lg = Long.parseLong(strbr.toString(), 2);//binary format to long (example:("1100101", 2) -> 101L)
						if (lg < 50000) { //this is work!				
							StringBuffer strb = new StringBuffer(); //assign sb1 to put byteData converted with hexadecimal representation
							for (int i = 0; i < byteData.length; i++) { //iterate through byteData and make the signed string
								strb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));//append it with hex format
							}

							String SHA256String = strb.toString(); //assign SHA256String and put strb string 
							byte[] digitalSignature = signData(SHA256String.getBytes()); //get the byte and go to function signData
							String SignedSHA256 = Base64.getEncoder().encodeToString(digitalSignature);//convert byte to string

							block.setASHA256String(SHA256String); //to set the SHA256 string
							block.setASignedSHA256(SignedSHA256); //to set the signed SHA256
							block.setAVerificationProcessID("Process" + pid); //to set the verify process id

							Date date = new Date();    //For the timestamp to find when is verified
							String time = String.format("%1$s %2$tF.%2$tT", "", date); //format:YYYY-MM-DD.HH:MI:SEC.Milisecond
							block.setAVerifiedTime(time + "." + pid); //set the time when it is verified 
							bChain.add(block);//add the block to blockChain
							mcastNewBchain(blockId);//go to function multi-casting new BlockChain
							break;
						}
						if (this.bChainBlockIds.contains(blockId)) { break; } //if blockChainBlockIds has blockId -> break 
					}
				} catch(Exception e) {System.out.println("This is Exception.");}
			}
			System.out.flush(); //rush the stream
		}
	}

	//this code is a given sample code from professor in the class
	public boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
		// Verify signature in the data, signed with the privatekey of the given public key  
		Signature signer = Signature.getInstance("SHA1withRSA"); //get signer instance with SHA1withRSA algorithm
		signer.initVerify(key); //the public key of the identity whose signature will be verified.
		signer.update(data); //to update the signature
		return (signer.verify(sig));
	}

	//this code is a given sample code from professor in the class
	public byte[] signData(byte[] data) throws Exception {

		Signature signer = Signature.getInstance("SHA1withRSA");//get the instance of SHA1withRSA and assign signer
		signer.initSign(keyPair.getPrivate()); //it initialize the private key of key pair to sign
		signer.update(data); //update the data 
		return (signer.sign());// returns the signature bytes of all the data updated
	}

	private void mcastNewBchain(String blockId) throws Exception {  

		//this is tools for managing the XML binding, given sample code from professor
		JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class); //this is tool for managing the XML binding 
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); //after change the object to XML, format to string 
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //format with line breaks and indentation
		StringWriter swr = new StringWriter();
		for (int i = 0; i < bChain.size(); i++){ //iterate through blockChain 
			jaxbMarshaller.marshal(bChain.get(i), swr); //XML send to StringWriter swr
		}
		String fullBlock = swr.toString(); //assign fullBlock with string swr
		String XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"; //given same code from professor
		String wipeBlock = fullBlock.replace(XMLHeader, ""); //combine XMLHeader and empty string
		String XMLBlock = XMLHeader + "\n<BlockLedger>" + wipeBlock + "</BlockLedger>"; //to set the BlockLeder 
		byte[] data = XMLBlock.getBytes(); //get the space for XMLBlock
		InetAddress ipAddress = InetAddress.getLocalHost(); //get the local host address
		int[] portNums = {4820, 4821, 4822}; //designated port numbers 
		if (!bChainBlockIds.contains(blockId)) { //if blockChainBlockIds are not included blockId
			for (int portNum: portNums) { //iterate through the port numbers
				DatagramPacket packet = new DatagramPacket(data,data.length, ipAddress, portNum); //get the packet with this address and port number
				socket.send(packet); //send this packet through the socket
			}
		}
	}
}

class UnverifiedBServer implements Runnable {

	public boolean ON = true; //assign boolean 'On' to true
	private DatagramSocket socket = null; //initial UDP socket is null
	public BlockVerifyWorker bVerifyWorker; //assign bVerifyWorker
	public KeyPair keyPair; //assign keyPair to hold a public key & a private key
	private int pid; //assign pid as process id
	public boolean ROLL; //assign boolean 'ROLL' to true

	public UnverifiedBServer(int port, int processId) throws Exception { //constructor
		socket = new DatagramSocket(port); //
		keyPair = generateKeyPair(); //make public key and private key
		pid = processId; 
		bVerifyWorker = new BlockVerifyWorker(socket, pid, keyPair); 
		if (pid == 2) { //if process is 2(process2 trigger events)
			mCast("mcast keys"); //go to function for multi-casting 	
		}
		ROLL = false; //change the boolean value
	}

	//this code frame for given sample code from professor in the class
	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");//make pairs of public/private keys with RSA algorithm
		SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN"); //get a SecureRandom object with SHA1PRNG algorithm(java.security)
		keyGenerator.initialize(1024, rng);//initialize keyGenerator with key size 2014 and secure random object
		return (keyGenerator.generateKeyPair());//make keyGenerator of public/private keys 
	}

	public void updateBChain(LinkedList<BlockRecord> newBChainString, ArrayList<String> newBChainBlockIds) {
		//when new block chain is out, update blockChain to the thread 
		bVerifyWorker.updateBChain(newBChainString, newBChainBlockIds);
	}

	public void run() {

		byte[] buffer = new byte[102400]; //create an byte array for the packet
		DatagramPacket callPacket = new DatagramPacket(buffer, buffer.length);
		new Thread(bVerifyWorker).start(); //starts a thread for verifying the blocks
		while (ON) { //while it is true,
			try {      
				socket.receive(callPacket);//get the callPacket through the socket
			} catch (IOException e) {System.out.println("IOException");} //catch the IOException
			String message = new String(buffer, 0, callPacket.getLength());//create message object

			if (message.toUpperCase().indexOf("BLOCKRECORD") > -1) {//if "BlockRecord" is include in the record
				try {
					bVerifyWorker.addBlock(message);//add block to verify	
				} catch (JAXBException e) {System.out.println("JAXBException");} //catch the JAXBException

			} else if (message.indexOf("mcast keys") > -1) { //if "multicast keys" is include in the record
				System.out.println("Releasing process " + pid + " public key"); //print out process id(2) and public key
				String data = "public key: Process" + pid + "|" + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
				// assign string data with public key: Process + process id + encoded public key
				try { mCast(data);} catch (IOException e) {System.out.println("IOException");} //try and catch statement

			} else if (message.indexOf("public key: ") > -1) { //if "public key: " is include in the record
				String[] data = message.replace("public key: ","").split("[|]"); //assign string array data and replace with public key   
				try {//decode with byte array
					byte[] decodedCreatorPublicKey = Base64.getDecoder().decode(data[1]);//change from string format to byte format(decode)
					//given sample code from professor, it makes a new X509EncodedKeySpec with the decodedCreatorPublicKey
					X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedCreatorPublicKey);  //Creates keySpec object with the given decodedCreatorPublicKe
					PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec); //byte to public key
					bVerifyWorker.publicKeys.put(data[0], publicKey);//put the data array(key) and publicKye(key value)
					System.out.println("Received " + data[0] + " public key"); //print out the line
				} catch(Exception e) {System.out.println("This is Exception.");} //catch the Exception

				if(bVerifyWorker.publicKeys.size() == 3) {//if the size equal to 3
					try { 
						readAndMcast("BlockInput" + pid + ".txt");//go to function readAndMcast with parameter (ex)BlockInput0.txt)
					} catch (Exception e) {System.out.println("Exception");} //catch the Exception
					ROLL = true; // boolean 'ROLL' value changes to false
				}
			}
		}
	}  

	public void mCast(String message) throws UnknownHostException, IOException {
		byte[] data = message.getBytes(); //get multi-casting message by byte array
		InetAddress ipAddress = InetAddress.getLocalHost(); //get the local host address
		int[] portNums = {4710, 4711, 4712}; //designated port numbers
		for (int portNum: portNums) {//iterate through the port numbers 
			DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, portNum); //get the packet with this address and port number
			socket.send(packet); //send packet through the socket
		}
	}

	public int readAndMcast(String filename) throws Exception {
		// many lines of code here from professor 
		int sum = 0; //start with 0

		//BufferedReader br = new BufferedReader(new FileReader(filename));
		//this is tool for managing the XML binding, given sample code from professor
		JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class); //this is tool for managing the XML binding 
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); //after change the object to XML, format to string 
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//format with line breaks and indentation

		String line; //assign string line 
		BufferedReader br = new BufferedReader(new FileReader(filename)); // using bufferedReader for reading file
		while ((line = br.readLine()) != null) { //while the line equal to null, read
			BlockRecord b = new BlockRecord(); //create new BlockRecord object b
			String suuid = new String(UUID.randomUUID().toString());//make unique id 
			b.setABlockID(suuid); //set blockId with unique id
			String signedBlockID = Base64.getEncoder().encodeToString(signData(suuid.getBytes()));//sign the block
			b.setASignedBlockID(signedBlockID);//set the signed block
			b.setACreatingProcess("Process" + pid); //create the process with process id

			//this is the timeStamp to figure out which process do first, given code from professor
			Date date = new Date();
			String time = String.format("%1$s %2$tF.%2$tT", "", date); //format:YYYY-MM-DD.HH:MI:SEC.Milisecond
			b.setACreatedTime(time + "." + pid); //set the timestamp as creadted block time

			/** Set data provided a file. CDE put the file data into the block record: */ 
			//input the tokens 0,1,2,3,4,5,6
			String[] tokens = line.split(" +");
			b.setFFName(tokens[0]); b.setFLName(tokens[1]); b.setFDOB(tokens[2]);
			b.setFSSNum(tokens[3]); b.setGDiag(tokens[4]); b.setGTreat(tokens[5]);
			b.setGRx(tokens[6]);

			StringWriter swr = new StringWriter(); //assign StringWriter swr object 
			jaxbMarshaller.marshal(b, swr); //XML send to StringWriter swr
			String partFullBlock = swr.toString(); //assign partFullBlock with string swr  
			MessageDigest md = MessageDigest.getInstance("SHA-256");//make SHA-256 instance through MessageDigest class
			md.update(partFullBlock.getBytes()); //go to update function
			byte[] byteData = md.digest();//byteData array of bytes with hash value.
			StringBuffer sb = new StringBuffer(); //assign StringBuffer sb object 
			for (int i = 0; i < byteData.length; i++) { // iterate through byteDate array
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));//append it with hex format
			}
			String SHA256String = sb.toString(); //assign SHA256String with string sb
			byte[] digitalSignature = signData(SHA256String.getBytes()); //digital signature  array with byte SHA256String
			String SignedSHA256 = Base64.getEncoder().encodeToString(digitalSignature); //assign SignedSHA256 with digitalSignature
			b.setADataHash(SHA256String); //set the data hash 
			b.setASignedDataHash(SignedSHA256); //set the signed data hash
			StringWriter stwr = new StringWriter(); //assign new StringWriter stwr object
			jaxbMarshaller.marshal(b, stwr); //XML send to StringWriter stwr
			String fullBlock = stwr.toString(); //assign fullBlock with string stwr
			byte[] data = fullBlock.getBytes(); //assign data with fullblock by byte array
			InetAddress ipAddress = InetAddress.getLocalHost(); //get the local host address

			int[] portNums = {4710, 4711, 4712}; //designated port numbers 
			for (int portNum: portNums) { //iterate through port numbers
				DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, portNum);//get the packet with this address and port number
				socket.send(packet); //send the packet through the socket
			}
			sum++; //increment sum
		}
		br.close(); //close to BufferReader
		return sum; 
	}

	//this code was given sample code from professor in the class
	public byte[] signData(byte[] data) throws Exception {
		Signature signer = Signature.getInstance("SHA1withRSA");//get signer instance with SHA1withRSA algorithm
		signer.initSign(keyPair.getPrivate());//get the private key and initialize signer for signing
		signer.update(data); //update the data
		return (signer.sign());
	}
}

public class Blockchain {

	public static boolean switchOn = true;

	public static void main(String args[]) throws Exception {
		//this is from given same code from professor
		int PID; //put an initial process id 
		PID = (args.length < 1) ? 0 : Integer.parseInt(args[0]); //if there is an argument, convert PID to int value
		System.out.println("Sojeong's BlockFramework start!! to quit (ctrl+c).\n");
		System.out.println("Using processID " + PID + "\n");
		if (args.length < 1) PID = 0; //no argument -> process id equals 0
		else if (args[0].equals("0")) PID = 0; //if argument is 0, process id equals 0
		else if (args[0].equals("1")) PID = 1; //if argument is 1, process id equals 1
		else if (args[0].equals("2")) PID = 2; //if argument is 2, process id equals 2
		else PID = 0; //else process id equals 0

		int unverifiedBlockPort = 4710 + PID; //calculate port 4710  
		int blockChainPort = 4820 + PID; //calculate port 4720 

		System.out.println("Working for all processes to release public keys.");

		UnverifiedBServer unverifiedBServer = new UnverifiedBServer(unverifiedBlockPort, PID);
		BChainServer bChainServer = new BChainServer(blockChainPort, unverifiedBServer, PID);
		Thread bChainThread = new Thread(bChainServer);  //new thread for block chain
		Thread unverifiedBlockThread = new Thread(unverifiedBServer); //new thread for unverified block
		bChainThread.start(); //new thread for block chain starts
		unverifiedBlockThread.start(); //new thread for unverified block starts

		while (switchOn) { //while it is true
			if (unverifiedBServer.ROLL) //if unverifiedBServer boolean value is true -> break
				break;
			System.out.flush();
		}
		//display options in the terminal
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\nPlease pick one of this options...\n");
		System.out.println("[C] -> Display the credit.");
		System.out.println("[R <filename>] -> Read a file of records.");
		System.out.println("[V <hash>] -> Verify the entire blockchain. ");
		System.out.println("[L] -> List the current blockchain");
		String opt;
		//using do-while loop
		do {
			opt = in.readLine(); //read command line 
			if(opt.contains("R ")) { //if you pick the R option
				String filename = opt.split("[ ]")[1];
				int sum = unverifiedBServer.readAndMcast(filename);
				System.out.println(sum + " records have been added to unverified blocks.");
			} else {
				switch(opt) { //depends on option that you choose, go to the case	
				case ("C") : //if you pick the C option
					bChainServer.getCredit(); //go to the getCredi() function
				break;
				case ("V") : //if you pick the V option
					bChainServer.verifyBChain(0); //go to the verifyBlockChain(0) function
				break;
				case ("V hash") : //if you pick the V hash option
					bChainServer.verifyBChain(1); //go to the verifyBlockChain(1) function
				break;
				case ("L") : //if you pick the L option
					bChainServer.listBChain(); //go to the listBlockChain() function
				break;
				default:
					System.out.println("Sorry, please choose the above options. '" + opt + "' is not an option.");		
				}  
			}
		} while (switchOn);          
	}      
}
