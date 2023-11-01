import mpi.*; // Импорт всех классов из библиотеки MPI

import java.util.Arrays; // Импорт класса для работы с массивами
import java.util.Random; // Импорт класса для генерации случайных чисел

public class Main { // Объявление класса Main
   public static void main(String[] args) throws Exception{ // Главный метод, который выполняется при запуске программы
       MPI.Init(args); // Инициализация MPI
       int myrank = MPI.COMM_WORLD.Rank(); // Получение ранга текущего процесса
       int size = MPI.COMM_WORLD.Size(); // Получение общего числа процессов
       int s = myrank; // Сохранение ранга в переменную s
       int buf[] = new int[1]; // Создание буфера для передачи данных
       int TAG = 0; // Объявление идентификатора для передачи данных

       int centre = size/2; // Вычисление центра процессов
       Random random = new Random(); // Создание объекта для генерации случайных чисел

       Request sendRequest = null; // Объявление запроса на отправку данных
       Request recvRequest = null; // Объявление запроса на получение данных
       if (myrank ==0){ // Если текущий процесс является первым
           int buf1[] = new int[centre-1]; // Создание буфера для получения данных
           int buf2[] = new int[size-2-centre]; // Создание буфера для получения данных

           recvRequest = MPI.COMM_WORLD.Irecv(buf1, 0, centre-1, MPI.INT, centre, 1); // Получение данных от процесса с рангом "centre"
           recvRequest.Wait(); // Ожидание завершения операции получения данных
           System.out.println("OK 1 number"); // Вывод сообщения об успешном получении данных

           recvRequest = MPI.COMM_WORLD.Irecv(buf2, 0, size-2-centre, MPI.INT, size-1, 2); // Получение данных от процесса с рангом "size-1"
           recvRequest.Wait(); // Ожидание завершения операции получения данных
           System.out.println("OK 2 number"); // Вывод сообщения об успешном получении данных

           int buf3[] = new int[size-3]; // Создание буфера для сортировки данных

           // Копирование элементов из первого массива в третий
           for (int i = 0; i < buf1.length; i++) {
               buf3[i] = buf1[i];
           }

           // Копирование элементов из второго массива в третий
           for (int i = 0; i < buf2.length; i++) {
               buf3[buf1.length + i] = buf2[i];
           }
           Arrays.sort(buf3); // Сортировка данных в третьем массиве

           String str = ""; // Создание строки для вывода результатов
           for (int i : buf3){
               str+=i+", ";
           }

           System.out.println("Result:"+str); // Вывод результатов
       }
       else if (myrank<centre) { // Если текущий процесс находится перед центром
           int randomInRange = random.nextInt(100) + 1; // Генерация случайного числа
           System.out.println("Process #"+myrank+" - "+randomInRange); // Вывод информации о процессе и сгенерированном числе
            sendRequest = MPI.COMM_WORLD.Isend(new int[]{randomInRange}, 0, 1, MPI.INT, centre, TAG); // Отправка данных процессу с рангом "centre"
      }
      else if (myrank > centre && myrank < size-1) { // Если текущий процесс находится после центра и перед последним
          int randomInRange = random.nextInt(100) + 1; // Генерация случайного числа
          System.out.println("Process #"+myrank+" - "+randomInRange); // Вывод информации о процессе и сгенерированном числе
          sendRequest = MPI.COMM_WORLD.Isend(new int[]{randomInRange}, 0, 1, MPI.INT, size -1, TAG); // Отправка данных последнему процессу
      }
      else if (myrank == centre) // Если текущий процесс является центральным
      {
          int new_buf[] = new int[centre-1]; // Создание буфера для получения данных
          int parcels_received = 0; // Создание переменной для подсчета полученных пакетов
          while(parcels_received != (centre-1)) { // Пока не получены все пакеты
              recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG); // Получение данных от любого процесса
              recvRequest.Wait(); // Ожидание завершения операции получения данных
              new_buf[parcels_received] = buf[0]; // Сохранение полученных данных в буфер
              parcels_received+=1; // Увеличение счетчика полученных пакетов
          }
          Arrays.sort(new_buf); // Сортировка данных в буфере
          String str = ""; // Создание строки для вывода результатов
          for (int i : new_buf){
              str+=i+", ";
          }
          sendRequest = MPI.COMM_WORLD.Isend(new_buf, 0, parcels_received, MPI.INT, 0, 1); // Отправка отсортированных данных первому процессу
      }
      else // Для всех остальных процессов
      {
          int new_buf[] = new int[size-centre-2]; // Создание буфера для получения данных
          int parcels_received = 0; // Создание переменной для подсчета полученных пакетов
          while(parcels_received != (size-centre-2)) { // Пока не получены все пакеты
              recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG); // Получение данных от любого процесса
              recvRequest.Wait(); // Ожидание завершения операции получения данных
              new_buf[parcels_received] = buf[0]; // Сохранение полученных данных в буфер
              parcels_received+=1; // Увеличение счетчика полученных пакетов
          }
          Arrays.sort(new_buf); // Сортировка данных в буфере
          String str = ""; // Создание строки для вывода результатов
          for (int i : new_buf){
              str+=i+", ";
          }
          sendRequest = MPI.COMM_WORLD.Isend(new_buf, 0, parcels_received, MPI.INT, 0, 2); // Отправка отсортированных данных первому процессу
          System.out.println("Sent the package:" + myrank+" - "+str); // Вывод информации о отправленных данных
      }
      MPI.Finalize(); // Завершение работы MPI
  }
}
