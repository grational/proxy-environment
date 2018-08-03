package it.italiaonline.rnd.proxy

enum RequestProtocol {
	ALL   { @Override String proxy() { System.getenv().get('all_proxy')   } },
	FTP   { @Override String proxy() { System.getenv().get('ftp_proxy')   } },
	HTTP  { @Override String proxy() { System.getenv().get('http_proxy')  } },
	HTTPS { @Override String proxy() { System.getenv().get('https_proxy') } },
	RSYNC { @Override String proxy() { System.getenv().get('rsync_proxy') } };
	abstract String proxy()
}
