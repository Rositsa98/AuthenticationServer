# AuthenticationServer
Final course project from Modern java technologies course, winter 2019. 

Authentication Server 
Да се имплементира прост authentication server.

Условие
Системата трябва да се състои от клиентска и сървърна част.

Сървър
Сървърът трябва да може да обслужва множество клиенти едновременно.
Сървърът трябва да предлага възможност за регистрация в системата. Регистрацията ще се извършва с username (уникален за базата на даден сървър), password, first name, last name, email.
User информацията трябва да се пази във файл (ще играе ролята на база от данни за сървъра), като паролата не трябва да се пази в plain text.
first name, last name и email полетата на user-a трябва да може да се редактират.
Паролата на user-a трябва да може да се reset-ва.
Клиент трябва да може да се автентицира пред сървъра със своето потребителско име и парола.
Сесията е обект, който пази уникален идентификатор и time-to-live(ttl). След изтичане на time-to-live периода, сесията бива унищожена. Системата трябва да може да създава нова сесия при успешна автентикация с потребителско име и парола и да връща уникално session id на клиента, както и ttl-а на сесията.
Системата трябва да позволява автентикация със session id, когато има успешно създадена сесия за даден user.
Системата трябва да позволява logout по дадено session id, като операцията трябва да унищожава съответната сесия.
При повторен login за даде user с username и password, предишната създадена сесия трябва да се терминира и да се създаде нова.
Системата трябва да предлага опция за изтриване на user, която да изтрива всяка пазена информация в базата за даденият user, както и да терминира всички създадени за него сесии.
Клиент
Клиентската част на приложението има възможността да консумира предлаганите от сървъра операции. Клиентът трябва да имплементира следните команди:

register --username <username> --password <password> --first-name <firstName> --last-name <lastName> --email <email>
login -–username <username> --password <password>
login -–session-id <sessionId>
reset-password –-username <username> --old-password <oldPassword> --new-password <newPassword>
update-user  -–session-id <session-id>  -–new-username <newUsername> --new-first-name <newFirstName> --new-last-name <newLastName> --new-email <email>. Всички параметри освен --session-id в тази команда са опционални.
logout –session-id <sessionId>
delete-user –username <username>
Submission
Качете .zip архив на познатите папки src, test и resources (опционално, ако имате допълнителни файлове, които не са .java) в sapera.org. Там няма да има автоматизирани тестове. Проектът ви трябва да е качен в грейдъра не по-късно от 18:00 в деня преди датата на защитата.

Успех!
