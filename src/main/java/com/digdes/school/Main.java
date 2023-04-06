package com.digdes.school;



public class Main {
    public static void main(String[] args) {
        try {
            JavaSchoolStarter starter = new JavaSchoolStarter();
            starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true");
            //Изменение значения которое выше записывали
            starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 wHeRe 'id'=3");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            System.out.println(starter.execute("SELECT"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
