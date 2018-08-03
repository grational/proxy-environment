package it.italiaonline.rnd.proxy

import spock.lang.*
import it.italiaonline.rnd.specification.Environment

/**
 * This Spock specification was auto generated by 'gigawatt'
 * @author d7392
 * @date 01-08-2018 12.55
 */
class EnvProxySpec extends Specification {

	def "Should raise an exception if a host is requested with a Proxy.Type.DIRECT"() {
		setup:
			def envProxy = new EnvProxy(RequestProtocol.FTP.proxy()) // no ftp_proxy set
		expect:
			envProxy.type() == Proxy.Type.DIRECT
		when:
			envProxy.host()
		then:
			def exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return a host with Proxy.Type.DIRECT'
		when:
			envProxy.port()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return a port with Proxy.Type.DIRECT'
	}

	def "Should raise an exception if the environment variable contains an invalid proxy"() {
		setup:
			new Environment (
				all_proxy: all_proxy
			).insert()
		when:
			new EnvProxy(RequestProtocol.ALL.proxy()).type()
		then:
			def exception = thrown(UnsupportedOperationException)
			exception.message == "Invalid proxy type '${all_proxy}'"
		where:
			all_proxy << [ 'https://proxy-dmz.pgol.net', 'proxy-dmz.pgol.net', '123stella', '_', '   ', '' ]
	}

	@Unroll
	def "Should correctly read the proxy for the request protocol #requestProtocol"() {
		setup:
			new Environment (
				all_proxy:   'http://proxy-all.pgol.net:1010',
				ftp_proxy:   'socks4://proxy-ftp.pgol.net:2020',
				http_proxy:  'http://proxy-http.pgol.net:3030',
				https_proxy: 'socks5://proxy-https.pgol.net:4040',
				rsync_proxy: 'http://proxy-rsync.pgol.net:5050'
			).insert()
		when:
			def envProxy = new EnvProxy(requestProtocol.proxy())
		then:
			envProxy.type() == expectedType
			envProxy.host() == expectedHost
			envProxy.port() == expectedPort
		where:
			requestProtocol       || expectedType     | expectedHost           | expectedPort
			RequestProtocol.ALL   || Proxy.Type.HTTP  | 'proxy-all.pgol.net'   | 1010
			RequestProtocol.FTP   || Proxy.Type.SOCKS | 'proxy-ftp.pgol.net'   | 2020
			RequestProtocol.HTTP  || Proxy.Type.HTTP  | 'proxy-http.pgol.net'  | 3030
			RequestProtocol.HTTPS || Proxy.Type.SOCKS | 'proxy-https.pgol.net' | 4040
			RequestProtocol.RSYNC || Proxy.Type.HTTP  | 'proxy-rsync.pgol.net' | 5050
	}

}