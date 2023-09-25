import mpi.*;

public class Main {
    public static void main(String[] args) {
        // идентификатор процесса, кол-во процессов, сообщение
        int myrank, size, message;
        // при приёме
        int TAG = 0;
      
        MPI.Init(args);
        myrank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();
        message = myrank;

        // Упаковать message в объект типа Integer
        Integer[] messageObj = new Integer[]{message};

        if ((myrank % 2) == 0) {
            if ((myrank + 1) != size)
                MPI.COMM_WORLD.Send(messageObj, 0, 1, MPI.OBJECT, myrank + 1, TAG);
        } else {
            if (myrank != 0) {
                MPI.COMM_WORLD.Recv(messageObj, 0, 1, MPI.OBJECT, myrank - 1, TAG);
                message = messageObj[0];
            }
            System.out.printf("received :%d\n", message);
        }
        MPI.Finalize();
    }
}
