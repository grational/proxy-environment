package it.grational.proxy

class NoProxy implements ExclusionList {
	private final List list

	NoProxy() {
		this.list = System.getenv().get('no_proxy')?.split(',') ?: []
	}

	@Override
	Boolean exclude(URL url) {
		return ( this.list.find { domain -> url.host.endsWith(domain) } != null )
	}

}
