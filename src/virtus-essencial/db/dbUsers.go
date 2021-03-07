package db

import (
//"log"
)

func createUsers() {
	sql := "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
		" SELECT 'aria', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
		" 'aria@gmail.com', '61 984385415', 'Ária Ohashi', 1, 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'aria')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
		" SELECT 'masaru', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
		" 'masaru@gmail.com', '61 984385415', 'Masaru Ohashi Jr', 6, 1, GETDATE()  " +
		" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'masaru')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
		" SELECT 'arnaldo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
		" 'arnaldo@gmail.com', '61 984385415', 'Arnaldo Burle', 1, 1, GETDATE()  " +
		" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'arnaldo')"
	db.Exec(sql)
	// --------------------------
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'alessandro.vaine', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','alessandro.vaine@previc.gov.br', '', 'ALESSANDRO VAINE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'alessandro.vaine')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'alexandre', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ALEXANDRE BISPO NUNES GRECO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'alexandre')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'alfredo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ALFREDO SULZBACHER WONDRACEK', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'alfredo')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'alvaro', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ALVARO RODRIGUES DOS SANTOS NETO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'alvaro')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'andre.silva', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ANDRE LUIS SOUZA DA SILVA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'andre.silva')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'andre.goncalves', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','andre.goncalves@previc.gov.br', '', 'ANDRE MACHADO GONÇALVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'andre.goncalves')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'angelica.campinho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','angelica.campinho@previc.gov.br', '', 'ANGELICA DE ALMEIDA FONSECA CAMPINHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'angelica.campinho')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'annette.pinto', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','annette.pinto@previc.gov.br', '', 'ANNETTE LOPES PINTO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'annette.pinto')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'antonio.portes', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ANTONIO ALBERTO GROSSI PORTES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'antonio.portes')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'antonio.garcia', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ANTONIO AUGUSTO GARCIA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'antonio.garcia')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'antonio.frainer', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ANTONIO SEVERO FRAINER', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'antonio.frainer')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'arnaldo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ARNALDO FERREIRA LEITE BURLE NETO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'arnaldo')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'carlos.buccos', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','carlos.buccos@previc.gov.br', '', 'CARLOS EDUARDO BUCCOS SILVEIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'carlos.buccos')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'carlos.neves', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'CARLOS EUGÊNIO D'AVILA NEVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'carlos.neves')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'carlos', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'CARLOS MARNE DIAS ALVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'carlos')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'charles.diniz', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','charles.diniz@previc.gov.br', '', 'CHARLES DINIZ BOTELHO DA SILVA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'charles.diniz')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'charles.dantas', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','charles.dantas@previc.gov.br', '', 'CHARLES SILVA DANTAS', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'charles.dantas')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'chow', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'CHOW CHI KWAN', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'chow')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'clovis.coelho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','clovis.coelho@previc.gov.br', '', 'CLOVIS GUIMARAES COELHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'clovis.coelho')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'dagomar', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'DAGOMAR ALECIO ANHE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'dagomar')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'dauto', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'DAUTO BARBOSA DE SOUSA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'dauto')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'david.coutinho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','david.coutinho@previc.gov.br', '', 'DAVID PRATES COUTINHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'david.coutinho')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'delma', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'DELMA PIRES GALVÃO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'delma')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'diogo.araujo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','diogo.araujo@previc.gov.br', '', 'DIOGO BORBA DE ARAUJO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'diogo.araujo@previc.gov.br')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'douglas', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'DOUGLAS CARVALHO DUARTE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'douglas')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'eduardo.meireles', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','eduardo.meireles@previc.gov.br', '', 'EDUARDO MENEZES MEIRELES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'eduardo.meireles')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'elianeoliveira.costa', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','elianeoliveira.costa@previc.gov.br', '', 'ELIANE OLIVEIRA DA COSTA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'elianeoliveira.costa')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'elyson', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ELYSON MARCEL TOME SCAFATI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'elyson')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'enaide', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ENAIDE MARIA TEIXEIRA DE SOUZA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'enaide')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'estevam.brayn', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','estevam.brayn@previc.gov.br', '', 'ESTEVAM BRAYN', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'estevam.brayn')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'evelyn', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'EVELYN DE QUEIROZ ITO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'evelyn')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'felipe', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'FELIPE SPOLAVORI MARTINS', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'felipe')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'francisco.arruda', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','francisco.arruda@previc.gov.br', '', 'FRANCISCO HELIO ARRUDA COELHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'francisco.arruda')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'francisco.junior', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'FRANCISCO RODRIGUES BRAGA JÚNIOR', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'francisco.junior')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'germano.barreira', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','germano.barreira@previc.gov.br', '', 'GERMANO CAVALCANTI BARREIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'germano.barreira')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'giselle', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'GISELLE LOPES MIRANDA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'giselle')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'hamilton', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'HAMILTON NOLETO MOREIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'hamilton')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'helvio', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'HELVIO ANTONIO PEREIRA MARINHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'helvio')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'hilton', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'HILTON DE ENZO MITSUNAGA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'hilton')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'humberto', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'HUMBERTO DA SILVA JUNIOR', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'humberto')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'isabel.cmaia', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','isabel.cmaia@previc.gov.br', '', 'ISABEL CRISTINA MAIA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'isabel.cmaia')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'izabel', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'IZABEL CRISTINA REZENDE NEVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'izabel')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'james', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JAMES TAYLOR FARIA CHAVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'james')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jorge', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JORGE LUIZ FONSECA FRISCHEISEN', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jorge')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jose.chedeak', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JOSÉ CARLOS SAMPAIO CHEDEAK', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jose.chedeak')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jose.pires', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JOSE ERALDO PIRES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jose.pires')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jose.cestari', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','jose.cestari@previc.gov.br', '', 'JOSE MARCOS CARVALHO CESTARI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jose.cestari')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jose.fernanes', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JOSÉ RICARDO FERREIRA FERNANDES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jose.fernanes')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'jucinea', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'JUCINEA DAS MERCES NASCIMENTO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'jucinea')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'juliana.pereira', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','juliana.pereira@previc.gov.br', '', 'JULIANA HELENA DE PAIVA PEREIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'juliana.pereira')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'leonardo.alves', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','leonardo.alves@previc.gov.br', '', 'LEONARDO FROTA ALVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'leonardo.alves')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luciano.draghetti', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','luciano.draghetti@previc.gov.br', '', 'LUCIANO DRAGHETTI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luciano.draghetti')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luciano.pinheiro', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'LUCIANO VILELA PINHEIRO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luciano.pinheiro')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luis.pugliese', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'LUIS ANTONIO ALVES PUGLIESE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luis.pugliese')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luis.barbosa', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'LUIS GUSTAVO DA CUNHA BARBOSA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luis.barbosa')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luis.angoti', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'LUÍS RONALDO MARTINS ANGOTI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luis.angoti')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'luiz', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'LUIZ ALBERTO GONÇALVES FIALHO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luiz')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'maique', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MAIQUE PEREIERA AGNES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'maique')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marcelo.melo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MARCELO FREITAS TOLEDO DE MELO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marcelo.melo')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marcelo.wajsenzon', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','marcelo.wajsenzon@previc.gov.br', '', 'MARCELO ZELIK WAJSENZON', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marcelo.wajsenzon')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marcia.vivas', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','marcia.vivas@previc.gov.br', '', 'MÁRCIA VIVAS DE ARAÚJO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marcia.vivas')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marcio', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MARCIO MATHEUS GUIMARAES MACHADO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marcio')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marcus', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MARCUS VINÍCIUS PINTO DE SOUZA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marcus')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'maria', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MARIA BATISTA DA SILVA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'maria')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'maria.fpimenta', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','maria.fpimenta@previc.gov.br', '', 'MARIA DA GLÓRIA FERREIRA PIMENTA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'maria.fpimenta')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'maria.cherulli', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','maria.cherulli@previc.gov.br', '', 'MARIA JULIETA CHERULLI MACHADO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'maria.cherulli')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'marina', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'MARINA MARCONDES DA SILVA ALVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'marina')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'mauricio.nakata', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','mauricio.nakata@previc.gov.br', '', 'MAURICIO DE AGUIRRE NAKATA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'mauricio.nakata')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'mauricio.lundgren', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','mauricio.lundgren@previc.gov.br', '', 'MAURICIO TIGRE VALOIS LUNDGREN', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'mauricio.lundgren')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'maury.oliveira', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','maury.oliveira@previc.gov.br', '', 'MAURY COELHO DE OLIVEIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'maury.oliveira')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'nercilia', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'NERCILIA BARROS DE SOUZA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'nercilia')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'nivea', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'NIVEA CLEIDE FERREIRA DOS SANTOS', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'nivea')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'otavio.reis', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','otavio.reis@previc.gov.br', '', 'OTAVIO LIMA REIS', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'otavio.reis')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'patricia', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'PATRICIA CERQUEIRA MONTEIRO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'patricia')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'paulo.matsumoto', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','paulo.matsumoto@previc.gov.br', '', 'PAULO ANDRÉ HIDEAKI MATSUMOTO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'paulo.matsumoto')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'paulo.diniz', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'PAULO NOBILE DINIZ', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'paulo.diniz')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'paulo.vitorino', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'PAULO VITORINO SILVA DE SOUSA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'paulo.vitorino')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'pedro', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'PEDRO IWAO KAKITANI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'pedro')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'pedro.pauloeugenio', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','pedro.pauloeugenio@previc.gov.br', '', 'PEDRO PAULO EUGENIO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'pedro.pauloeugenio')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'peterson.goncalves', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','peterson.goncalves@previc.gov.br', '', 'PETERSON GONCALVES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'peterson.goncalves')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'rafael', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'RAFAEL MAGALHÃES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'rafael')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'raquel.gerhardt', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','raquel.gerhardt@previc.gov.br', '', 'RAQUEL GERHARDT', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'raquel.gerhardt')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'rita', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'RITA DE CASSIA CORREA DA SILVA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'rita')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'roberto.sakamoto', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','roberto.sakamoto@previc.gov.br', '', 'ROBERTO SAKAMOTO', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'roberto.sakamoto')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'rodrigo.oliveira', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','rodrigo.oliveira@previc.gov.br', '', 'RODRIGO AIRES DE OLIVEIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'rodrigo.oliveira')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'rodrigo.abreu', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','rodrigo.abreu@previc.gov.br', '', 'RODRIGO MARTINS ABREU', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'rodrigo.abreu')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'rodrigo.andrade', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','rodrigo.andrade@previc.gov.br', '', 'RODRIGO RANGEL DE ANDRADE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'rodrigo.andrade')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'romulo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'ROMULO GONÇALVES DA SILVA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'romulo')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'sergio', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'SERGIO DJUNDI TANIGUCHI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'sergio')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'simone', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'SIMONE CORRÊA REIS', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'simone')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'vandeisa', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'VANDEISA MOURA ALMEIDA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'vandeisa')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'vanessa', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'VANESSA DA ROCHA VIER', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'vanessa')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'veronica', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'VERONICA SOUSA SILVEIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'veronica')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'vitor', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'VITOR FERNANDES RIBEIRO DE OLIVEIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'vitor')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'walter', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'WALTER DE CARVALHO PARENTE', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'walter')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'wander.mingardi', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','wander.mingardi@previc.gov.br', '', 'WANDER RICARDO MINGARDI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'wander.mingardi')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'wania.capparelli', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','wania.capparelli@previc.gov.br', '', 'WÂNIA MARIA FRANÇA CAPPARELLI', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'wania.capparelli')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'wellington.marques', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'WELINGTON RODRIGUES MARQUES', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'wellington.marques')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) SELECT 'wellington.pereira', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6','', '', 'WELLINGTON MARCOS ASSIS PEREIRA', 4, 1, GETDATE() WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'wellington.pereira')"
	db.Exec(sql)
	/*
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'fulano', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'fulano@gmail.com', '61 984385415', 'Fulano de Tal', 2, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'fulano')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'sicrano', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'sicrano@gmail.com', '61 984385415', 'Sicrano de Tal', 3, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'sicrano')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'beltrano', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'beltrano@gmail.com', '61 984385415', 'Beltrano de Tal', 4, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'beltrano')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'huguinho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'huguinho@gmail.com', '61 984385415', 'Huguinho da Silva', 2, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'huguinho')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'zezinho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'zezinho@gmail.com', '61 984385415', 'Zezinho da Silva', 3, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'zezinho')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'luisinho', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'luisinho@gmail.com', '61 984385415', 'Luisinho de Tal', 4, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'luisinho')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'athos', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'athos@gmail.com', '61 984385415', 'Athos Mosqueteiro', 2, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'athos')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'porthos', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'porthos@gmail.com', '61 984385415', 'Porthos Mosqueteiro', 3, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'porthos')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'aramis', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'aramis@gmail.com', '61 984385415', 'Aramis Mosqueteiro', 4, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'aramis')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'zuenir', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'zuenir@gmail.com', '61 984385415', 'Zuenir Ventura', 2, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'zuenir')"
		db.Exec(sql)
		sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
			" SELECT 'verissimo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
			" 'verissimo@gmail.com', '61 984385415', 'Luís Fernando Veríssimo', 3, 1, GETDATE() " +
			" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'verissimo')"
		db.Exec(sql)*/
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
		" SELECT 'james', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
		" 'james@gmail.com', '61 984385415', 'James Taylor', 4, 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'james')"
	db.Exec(sql)
	sql = "INSERT INTO virtus.users (username, password, email, mobile, name, id_role, id_author, criado_em) " +
		" SELECT 'leonardo', '$2a$10$4Q6xe3fxi1HC8PX7LSGIkeYpkNa9OdpUv3rUD0ht4g3Fu6gJgg1g6', " +
		" 'leofiuza@gmail.com', '61 984385415', 'Leonardo Fiuza', 4, 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id_user FROM virtus.users WHERE username = 'leonardo')"
	db.Exec(sql)
}
