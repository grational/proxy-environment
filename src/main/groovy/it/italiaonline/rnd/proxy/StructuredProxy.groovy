package it.italiaonline.rnd.proxy

import java.net.Proxy

interface StructuredProxy {
	Proxy.Type type()
	String     host()
	Integer    port()
	Boolean    secure()
	Boolean    auth()
	String     username()
	String     password()
}
