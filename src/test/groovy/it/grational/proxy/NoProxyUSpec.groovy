package it.grational.proxy

import spock.lang.*
import it.grational.specification.Environment

class NoProxyUSpec extends Specification {

	def "Should be capable of saying if a certain URL needs to pass through proxy or not"() {
		setup:
			new Environment (
				no_proxy: '10.0.0.2,pgol.net'
			).insert()

		expect:
			new NoProxy().exclude(url.toURL()) == expectedValue

		where:
			url                                            || expectedValue
			'http://gitlab.pgol.net'                       || true
			'https://gitlab.com'                           || false
			'ftp://10.0.0.2'                               || true
			'http://10.0.0.1'                              || false
			'https://some.domain.it/query?param=1&param=2' || false
			'http://gitlab.pgol.net/query?param=1&param=2' || true
	}

}
