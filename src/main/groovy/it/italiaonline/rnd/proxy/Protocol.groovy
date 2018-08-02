package it.italiaonline.rnd.proxy

enum Protocol {
	ALL   { @Override String envVar() { 'all_proxy'   } },
	FTP   { @Override String envVar() { 'ftp_proxy'   } },
	HTTP  { @Override String envVar() { 'http_proxy'  } },
	HTTPS { @Override String envVar() { 'https_proxy' } },
	RSYNC { @Override String envVar() { 'rsync_proxy' } };
	abstract String envVar()
}
