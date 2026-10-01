package CodiceV2;
public class ReadersWritersHandler {

    public class Semaphore {
        private int s;

        public Semaphore(int v){
            s=v;
        }

        public Semaphore() {this(1);}

        synchronized public void p(){
            while (s<=0)
                try{
                    wait();

                }catch (InterruptedException e){}

            s--;
        }

        synchronized public void v(){
            s++;
            notifyAll();
        }
    }

    private int ar = 0; // active readers
    private boolean aw = false; // active writer
    private int br = 0; // blocked readers
    private int bw = 0; // blocked writers
    private Semaphore rs = new Semaphore(0); // priv. sem. readers
    private Semaphore ws = new Semaphore(0); // priv. sem. writers
    private Semaphore df = new Semaphore(1); // priv. sem. delete
    private Semaphore rf = new Semaphore(1); // priv. sem. rename

    public boolean checkForWriters(){
        return aw;
    }

    public boolean checkForReaders(){
        if(ar!=0)
            return true;
        else
            return false;
    }

    public int getAr(){

        return ar;
    }

    public int getBr(){

        return br;
    }

    public int getBw(){

        return bw;
    }


    public int isEdited(){
        if(aw==true)
            return 1;
        else
            return 0;
    }

    public void beginRead(){

        synchronized (this){
            if(!aw && (bw==0)){ //no active writers and no blocked writers
                rs.v();
                ar++;

            }
            else{
                br++;

            }
        }
        rs.p();
        //unlock reader

    }

    synchronized public void endRead(){
        ar--;
        if(ar==0 && bw> 0 ){ //last reader quits and there are writers waiting
            aw=true;
            bw--;
            ws.v();

        }
    }

    public void beginWrite(){

        synchronized(this){
            if(ar==0 && !aw){ //no active readers and active writers=fasle
                ws.v();
                aw=true;

            }


            else{
                bw++;
            }
        }
        ws.p();
        //unlock writer
    }

    synchronized public void endWrite(){
        aw=false;
        if(br>0) while (br>0){ //blocked readers > 0
            br--; //remove blocked readers
            ar++; //add active readers
            rs.v();

        }
        else if(bw>0){ //blocked writers > 0
            aw=true;
            bw--; //remove blocked writers
            ws.v();

        }
    }

    synchronized public void beginRename() {

            if(!aw && ar==0 && br==0 && bw==0){ //empty handler
                rf.p();
                aw=true;
            }

    }

    synchronized public void endRename(){
        aw=false;
        rf.v();
        if(br>0) while (br>0){ //blocked readers > 0
            br--;
            ar++;
            rs.v();

        }
        else if(bw>0){ //blocked writers > 0
            aw=true;
            bw--;
            ws.v();

        }
    }

    public void beginDelete() {

        if(!aw && ar==0 && br==0 && bw==0){ //empty handler
            df.p();
            aw=true;
        }
       	
    }

    synchronized public void endDelete(){
        aw=false;
        df.v();
        if(br>0) while (br>0){ //blocked readers > 0
            br--;
            ar++;
            rs.v();

        }
        else if(bw>0){ //blocked writers > 0
            aw=true;
            bw--;
            ws.v();

        }
    }

}
