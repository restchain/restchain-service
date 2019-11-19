INSERT INTO `user` VALUES (1,'0x535CCa8697F29DaC037a734D6984eeD7EA943A85','2019-11-11 20:55:47','$2a$10$921VxD7tOl2/rUa.UWPcOerycjHyvT1K1IKAqOWeVMvwJcxsv84x.'),(2,'0x9515365F4cB7463E7d0B9A12De7706dE6EB62709','2019-11-11 20:55:59','$2a$10$l2OfpkgBepGhns0dasoH/OrOmtuJM8A5QdJf0bwLMiXTKd8VTt8Ca'),(3,'0x901D7C8d516a5c97bFeE31a781A1101D10BBc8e9','2019-11-11 20:56:21','$2a$10$JeEQV4Vwm1LI/95V9XWxtuJL/muaeXREN.mUrmjBaqfy0xb0iEzd6'),(4,'0x84FdF08A7317c58AfBb9342636Ce1496C9Eb3B60','2019-11-11 20:56:32','$2a$10$k80XMjOZMokGfvMraDKXkOg44PE/LhoSDdybPqblJl6nSNBrMm5fq'),(5,'0x07ED3d24A545f85B04bFC5Cc26De59Dde920f9Fe','2019-11-11 20:56:43','$2a$10$xWlC5r9gJf7yuHpQZB/cuexr9rjga7eCHE/OK7KjBAkew.wrvEFlW');
INSERT INTO `participant` VALUES (1,'Buyer',1),(2,'Seller',1),(3,'Buyer',2),(4,'Seller',2);
INSERT INTO `instance` VALUES (1,'2019-11-11 20:58:40',1,1),(2,'2019-11-11 20:58:52',1,1),(3,'2019-11-11 21:01:19',2,2);
INSERT INTO `choreography` VALUES (1,'2019-11-11 20:58:24','Shop purchase model test','ShopNew.bpmn','ShopNew',1),(2,'2019-11-11 21:01:10','Old shop model maybe its bugged','ShopOld.bpmn','ShopOld',2);
INSERT INTO `instance_participant_user` VALUES (1,1,1,1),(2,1,2,2),(3,2,1,1),(4,3,4,2);


