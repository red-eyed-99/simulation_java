# Проект "Симуляция"
## Описание:
Проект представялет из себя пошаговую симуляцию 2D мира, населенного **травоядными** и **хищниками**, где задача травоядных найти и **съесть траву**, а задача хищников **охотиться** на травоядных😈.
По мимо существ на карте присутствуют **статичные объекты**, такие как **деревья**, **камни**, с которыми существа не взаимодействуют, а также объекты которые **служат пищей** для существ, 
такие как **трава**, и **мясо**.
___
<div align="center">
  <image src='https://github.com/user-attachments/assets/d0501435-7fed-4239-a711-a33e457a8cd0'/>
</div>
    
___
## Правила:
1. Каждый раз мир симуляции имеет **разный ландшафт**.
2. На одной клетке может находиться только **один объект**.
3. При необходимости **каждый ход** добавляется в **случайное место** трава или травоядное.
4. Каждый ход симуляции **существа** могут выполнить одно из следующих действий: **переместиться** на другую клетку, **съесть** пищу, **атаковать** существо, **пропустить** ход.
5. Существо может передвигаться по клеткам только **горизонтально или вертикально** на количество, соответствующее скорости.
6. Существа имеют различное количество очков **здоровья**, **скорость** передвижения (количество клеток, на которое существо может переместиться за один ход).
7. Чтобы **атаковать** существо или **съесть** пищу, существо должно находиться на **соседней клетке** от существа или еды.

<div align="center">
  <image src='https://github.com/user-attachments/assets/c81044ef-0c4a-441c-8dcc-5ec039dcb35b'/>
</div>
    
8. После **смерти** травоядного, на его месте появляется **мясо**, которое может **съесть** хищник.
9. На карте симуляции, есть **вода** по которой могут ходить существа, но скорость их передвижения **ограничивается** одной клеткой.
10. Симуляция идет **бесконечно**.
## Запуск:
### Способ 1
Запустить **Simulation.exe** из архива **Simulation.zip**
### Способ 2
- Установить **Java Development Kit** или **Java Runtime Environment** от версии **22.0.2**
- Установить **SDK JavaFX** https://gluonhq.com/products/javafx/. 
- Запустить **Simulation.jar** из **Simulation.zip**, выполнив команду в командной строке
  ```
  java --module-path /path/to/javafx/lib --add-modules java.base,javafx.base,javafx.controls,javafx.fxml -jar /path/to/Simulation.jar
  ```
### Способ 3 - в IDE Intellij Idea
  1. Убедиться, что установлен **JDK** необходимой версии.
  2. Установить **SDK JavaFX** https://gluonhq.com/products/javafx/.
  3. Открыть проект в **Intellij Idea**.
  4. Перейти в **Main Menu** ➔ **File** ➔ **Project Structure** ➔ **Libraries**
  5. Добавить библиотеку **JavaFX** (**New Project Library** ➔ **указать путь к папке lib** ➔ **применить изменения**)
  6. Перейти в **Run/Debug Configurations** (**Main Menu** ➔ **Run** ➔ **Edit Configurations...**)
  7. Добавить новую конфиграцию (**Add New Configuration** ➔ **Application**)
  8. Настроить конфигурацию 
		- добавить **VM Options** (**Modify options** ➔ **Add VM Options**)
		- ввести в поле **VM Options** значение
    ```--module-path "/path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml```
		- выбрать **Main class** проекта, применить изменения
  9. Запустить проект.
