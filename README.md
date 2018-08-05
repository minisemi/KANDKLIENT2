<html>
<head>
<h1>Bachelors project</h1>
</head>
<body>

<p>The app is meant to function as a help tool for firemen in the field. A user first logs in with a valid NFC-card, but for demonstration
purposes I also implemented an invisible button in the middle of the screen on the enlarged icon-image which doesn't require a valid NFC-card.
The app then has different functions to prove the app's requirements (funktionella och icke-funktionella krav). The functions include:
<ul>
<li> a map which has editable markers which can be synced and seen by other logged in users</li>
<li> videocommunication with other logged in users using WebRTC</li>
<li> a voting system with a sender and receiver that can send encrypted and non-encrypted votes using a combination of RSA and AES with onion routing</li>
</ul>
The voting system was added to research performance of RSA/AES encryption in <a href="https://github.com/minisemi/KANDSERVER2">onion routing networks</a>, versus non-encrypted messages. The results of the voting system tests can be found <a href="http://www.diva-portal.org/smash/record.jsf?pid=diva2%3A1066541&dswid=-7324">here</a>.
</p>

</body>
</html>
