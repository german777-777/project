#<span style="color:greenyellow">***На данном этапе сделано:***</span>

* <span style="color:gold">*Полный перенос на Hibernate*</span>
****
* <span style="color:gold">*Удалены старые репозитории для нормальной работы версии Hibernate*</span>
****
* <span style="color:gold">*Исправлены "каскады" для таблиц, имеющих foreign key*</span>
****
* <span style="color:gold">*Общие методы (```create```, ```getByID```, ```update```, ```remove```)
  вынесены в абстрактный класс ```AbstractRepoJpa.java```*</span>


###<span style="color:greenyellow">***Примечания:***</span>

<span style="color:color:gold">*Есть баг: при удалении студента (или предмета) из группы находит студента (или предмет), идёт в репозиторий, но там не удаляет его*</span>
****
<span style="color:color:gold">*Есть баг: операция сохранения группы не работает через ```manager.persist()``` при ```CascadeType.PERSIST```. Иначе говоря, плохое сохранение*</span>


