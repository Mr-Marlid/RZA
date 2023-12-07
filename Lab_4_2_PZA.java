import mpi.MPI;
import mpi.MPIException;
import com.google.common.base.Stopwatch;

public class Lab_4_2_PZA {
    public static void main(String[] args) throws MPIException {
        Stopwatch stopwatch = Stopwatch.createStarted(); // Создаем и запускаем секундомер

        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int n1 = 10; // Количество строк в матрице A
        int n2 = 10; // Количество столбцов в матрице A и строк в матрице B
        int n3 = 10; // Количество столбцов в матрице B

        int[][] matrixA = new int[n1][n2];
        int[][] matrixB = new int[n2][n3];
        int[][] resultC = new int[n1][n3];

        // Инициализация матриц A и B
        if (rank == 0) {
            for (int i = 0; i < n1; i++) {
                for (int j = 0; j < n2; j++) {
                    matrixA[i][j] = i + j; // Здесь задайте значения элементов матрицы A
                }
            }

            for (int i = 0; i < n2; i++) {
                for (int j = 0; j < n3; j++) {
                    matrixB[i][j] = i + j; // Здесь задайте значения элементов матрицы B
                }
            }
        }

        // Асинхронная отправка матриц A и B на все процессы
        MPI.COMM_WORLD.Bcast(matrixA, 0, n1, MPI.OBJECT, 0);
        MPI.COMM_WORLD.Bcast(matrixB, 0, n2, MPI.OBJECT, 0);

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n3; j++) {
                resultC[i][j] = 0;
                for (int k = 0; k < n2; k++) {
                    resultC[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        // Асинхронная отправка результатов на процесс с рангом 0
        if (rank != 0) {
            MPI.COMM_WORLD.Isend(resultC, 0, n1, MPI.OBJECT, 0, 0);
        } else {
            for (int i = 1; i < size; i++) {
                int[][] receivedResult = new int[n1][n3];
                MPI.COMM_WORLD.Recv(receivedResult, 0, n1, MPI.OBJECT, i, 0);

                // Складываем полученные результаты
                for (int row = 0; row < n1; row++) {
                    for (int col = 0; col < n3; col++) {
                        resultC[row][col] += receivedResult[row][col];
                    }
                }
            }

            System.out.println("Result C:");
            printMatrix(resultC);
        }

        MPI.Finalize();

        stopwatch.stop(); // Останавливаем секундомер
        long executionTime = stopwatch.elapsed().toMillis(); // Получаем время выполнения в миллисекундах
        System.out.println("Execution time: " + executionTime + " milliseconds");

        // Сохраняем время выполнения на каждом процессе в массив
        long[] executionTimes = new long[size];
        MPI.COMM_WORLD.Gather(new long[] { executionTime }, 0, 1, MPI.LONG, executionTimes, 0, 1, MPI.LONG, 0);

        // Выводим среднее время выполнения
        if (rank == 0) {
            long totalExecutionTime = 0;
            for (long time : executionTimes) {
                totalExecutionTime += time;
            }
            double averageExecutionTime = (double) totalExecutionTime / size;
            System.out.println("Average execution time: " + averageExecutionTime + " milliseconds");
        }
    }

    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
