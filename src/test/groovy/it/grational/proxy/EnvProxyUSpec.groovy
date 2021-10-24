package it.grational.proxy

import spock.lang.*
import it.grational.specification.Environment

/**
 * This Spock specification was auto generated by 'gigawatt'
 * @author d7392
 * @date 01-08-2018 12.55
 */
class EnvProxyUSpec extends Specification {

	@Unroll
	def "Should raise an exception if the environment variable contains an invalid proxy: #all_proxy"() {
		setup:
			new Environment (
				all_proxy: all_proxy
			).insert()
		when:
			new EnvProxy(EnvVar.ALL.value())
		then:
			def exception = thrown(UnsupportedOperationException)
			exception.message == "Invalid proxy string '${all_proxy}'"
		where:
			all_proxy << [ 'https://proxy-dmz.pgol.net', 'proxy-dmz.pgol.net', '123stella', '_', '   ', '' ]
	}

	def "Should raise an exception if a proxy detail method is called with Proxy.Type.DIRECT"() {
		setup: 'unset the ftp_proxy environment variable'
			new Environment(ftp_proxy: '').clean()
		and:
			def envProxy = new EnvProxy(EnvVar.FTP.value())
		expect:
			envProxy.proxy() == Proxy.NO_PROXY
		and:
			envProxy.type() == Proxy.Type.DIRECT

		when:
			envProxy.host()
		then:
			def exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy host with a direct connection'

		when:
			envProxy.port()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy port with a direct connection'

		when:
			envProxy.secure()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy secure flag with a direct connection'

		when:
			envProxy.auth()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy auth flag with a direct connection'

		when:
			envProxy.username()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy username with a direct connection'

		when:
			envProxy.password()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy password with a direct connection'

		when:
			envProxy.toString()
		then:
			exception = thrown(UnsupportedOperationException)
			exception.message == 'Cannot return the proxy string representation with a direct connection'
	}

	@Unroll
	def "Should correctly acquire the proxy details got from the environment variable #envVar"() {
		setup:
			new Environment (
				all_proxy:   'https://proxy-all.pgol.net:1010',
				ftp_proxy:   'socks4://proxy-ftp.pgol.net:2020',
				http_proxy:  'http://proxy-http.pgol.net:3030',
				https_proxy: 'socks5://proxy-https.pgol.net:4040',
				rsync_proxy: 'https://proxy-rsync.pgol.net:5050'
			).insert()
		when:
			def envProxy = new EnvProxy(envVar.value())
		then:
			envProxy.proxy()    == expectedProxy
			envProxy.type()     == expectedType
			envProxy.host()     == expectedHost
			envProxy.port()     == expectedPort
			envProxy.secure()   == expectedSecureFlag
			envProxy.auth()     == expectedAuthFlag
			envProxy.toString() == expectedString
		where:
			envVar       | proxyType     | proxyString                          || expectedProxy                                                                    | expectedType     | expectedHost           | expectedPort | expectedSecureFlag | expectedAuthFlag | expectedString
			EnvVar.ALL   | 'all_proxy'   | 'https://proxy-all.pgol.net:1010'    || new Proxy(Proxy.Type.HTTP, new InetSocketAddress('proxy-all.pgol.net', 1010))    | Proxy.Type.HTTP  | 'proxy-all.pgol.net'   | 1010         | true               | false            | proxyString
			EnvVar.FTP   | 'ftp_proxy'   | 'socks4://proxy-ftp.pgol.net:2020'   || new Proxy(Proxy.Type.SOCKS, new InetSocketAddress('proxy-ftp.pgol.net', 2020))   | Proxy.Type.SOCKS | 'proxy-ftp.pgol.net'   | 2020         | false              | false            | proxyString
			EnvVar.HTTP  | 'http_proxy'  | 'http://proxy-http.pgol.net:3030'    || new Proxy(Proxy.Type.HTTP, new InetSocketAddress('proxy-http.pgol.net', 3030))   | Proxy.Type.HTTP  | 'proxy-http.pgol.net'  | 3030         | false              | false            | proxyString
			EnvVar.HTTPS | 'https_proxy' | 'socks5://proxy-https.pgol.net:4040' || new Proxy(Proxy.Type.SOCKS, new InetSocketAddress('proxy-https.pgol.net', 4040)) | Proxy.Type.SOCKS | 'proxy-https.pgol.net' | 4040         | false              | false            | proxyString
			EnvVar.RSYNC | 'rsync_proxy' | 'https://proxy-rsync.pgol.net:5050'  || new Proxy(Proxy.Type.HTTP, new InetSocketAddress('proxy-rsync.pgol.net', 5050))  | Proxy.Type.HTTP  | 'proxy-rsync.pgol.net' | 5050         | true               | false            | proxyString
	}

	@Unroll
	def "Should correctly handle an authenticated proxy got from the environemnt variable #envVar"() {
		setup:
			new Environment((proxyType): proxyString).insert()
		when:
			def envProxy = new EnvProxy(envVar.value())
		then:
			envProxy.type()     == expectedType
			envProxy.host()     == expectedHost
			envProxy.port()     == expectedPort
			envProxy.secure()   == expectedSecureFlag
			envProxy.auth()     == expectedAuthFlag
			envProxy.username() == expectedUsername
			envProxy.password() == expectedPassword
			envProxy.toString() == expectedString
		where:
			envVar       | proxyType     | proxyString                                                || expectedType     | expectedHost           | expectedPort | expectedSecureFlag | expectedAuthFlag | expectedUsername | expectedPassword | expectedString
			EnvVar.ALL   | 'all_proxy'   | 'https://user_all:pass_all@proxy-all.pgol.net:1010'        || Proxy.Type.HTTP  | 'proxy-all.pgol.net'   | 1010         | true               | true             | 'user_all'       | 'pass_all'       | proxyString
			EnvVar.FTP   | 'ftp_proxy'   | 'socks4://user_ftp:pass_ftp@proxy-ftp.pgol.net:2020'       || Proxy.Type.SOCKS | 'proxy-ftp.pgol.net'   | 2020         | false              | true             | 'user_ftp'       | 'pass_ftp'       | proxyString
			EnvVar.HTTP  | 'http_proxy'  | 'http://user_http:pass_http@proxy-http.pgol.net:3030'      || Proxy.Type.HTTP  | 'proxy-http.pgol.net'  | 3030         | false              | true             | 'user_http'      | 'pass_http'      | proxyString
			EnvVar.HTTPS | 'https_proxy' | 'socks5://user_https:pass_https@proxy-https.pgol.net:4040' || Proxy.Type.SOCKS | 'proxy-https.pgol.net' | 4040         | false              | true             | 'user_https'     | 'pass_https'     | proxyString
			EnvVar.RSYNC | 'rsync_proxy' | 'https://user_rsync:pass_rsync@proxy-rsync.pgol.net:5050'  || Proxy.Type.HTTP  | 'proxy-rsync.pgol.net' | 5050         | true               | true             | 'user_rsync'     | 'pass_rsync'     | proxyString
	}
}
