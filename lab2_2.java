import mpi.*;
public class Main {
    public static void main(String[] args)
            throws Exception{
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int s = myrank;
        int buf[] = new int[1];
        int TAG = 0;
        Request sendRequest = null;
        Request recvRequest = null;

        if (myrank ==0)
        {
            sendRequest = MPI.COMM_WORLD.Isend(new int[]{s}, 0, 1, MPI.INT, (myrank + 1) % size, TAG);
            System.out.println("sent the package:" + myrank);
            sendRequest.Wait();


            recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, (myrank - 1 + size) % size, TAG);
            recvRequest.Wait();

            System.out.println("accepted the package:" + myrank);
            System.out.println("Total sum = " + buf[0]);
        }
        else {
            recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, (myrank - 1 + size) % size, TAG);
            recvRequest.Wait();s +=buf[0];
            System.out.println("accepted the package:" + myrank);

            sendRequest = MPI.COMM_WORLD.Isend(new int[]{s}, 0, 1, MPI.INT, (myrank + 1) % size, TAG);
            System.out.println("sent the package:" + myrank);
            sendRequest.Wait();
        }
        MPI.Finalize();
    }
}
