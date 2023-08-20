DROP TABLE IF EXISTS `bbs_news`;
CREATE TABLE `bbs_news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL,
  `title_ru` text CHARACTER SET utf8 NOT NULL,
  `title_en` text CHARACTER SET utf8 NOT NULL,
  `text_ru` text CHARACTER SET utf8 NOT NULL,
  `text_en` text CHARACTER SET utf8 NOT NULL,
  `info_ru` varchar(32) CHARACTER SET utf8 NOT NULL,
  `info_en` varchar(32) CHARACTER SET utf8 NOT NULL,
  `author` varchar(32) CHARACTER SET utf8 NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
);
-- id = ID (Номер новости!) Внимание: Для каждой новости ID должен быть разным.
-- type = Тип Новости (0 = Новости проекта, 1 = Новости сервера)
-- title_ru = Заголовок новости (На Русском)
-- title_en = Заголовок новости (На Английском)
-- text_ru = Полный текст новости (На Русском)
-- text_en = Полный текст новости (На Английском)
-- info_ru = Краткая информация. К примеру: Сервер: x3 (На Русском)
-- info_en = Краткая информация К примеру: Server: x3 (На Английском)
-- author = Автор новости
-- date = Дата добовления. (Формат: YY.MM.DD \ 13(Год).10(Месяц).31(День) = 31.10.2013)
INSERT INTO `bbs_news` VALUES ('1', '0', 'Посетите наш форум.', 'Visit our forum.', 'На нашем форуме вы сможете сообщить об ошибках, найти полезную информацию, а также описание дополнительных платных услуга.<br>Адрес нашего форума: Open-Team.ru/forum/.', 'In the forum you will be able to bugs report, find helpful information and a description of additional paid service.<br>Address our forum: Open-Team.ru/forum/.', 'Open-Team', 'Open-Team', 'L2CCCP', '13.10.31');
INSERT INTO `bbs_news` VALUES ('2', '1', 'Спасибо за покупку нашей сборки.', 'Thank you for purchasing our Assembling.', 'Уважаемый клиент, благодарим вас за преобретение нашего продукта "Ява эмулятор Lineage 2: High Five", надеемся наше сотрудничество будет долгим и плодаттворным для обоих сторон.', 'Dear client, thank you for the obtaining of our product "Java emulator Lineage 2: High Five", we hope our cooperation will be long and productive for both sides.', 'Сервер: Test', 'Server: Test', 'L2CCCP', '13.10.31');
