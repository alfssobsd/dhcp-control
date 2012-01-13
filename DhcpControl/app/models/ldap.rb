class Ldap
  attr_reader :ldap
  
  def initialize(options = {})
    ldap_config  = YAML.load_file("#{Rails.root.to_s}/config/ldap.yml")[Rails.env]
    ldap_options = options
    ldap_options[:encryption] = :simple_tls if ldap_config["ssl"]

    @ldap = Net::LDAP.new(ldap_options)
    @ldap.host  = ldap_config["host"]
    @ldap.port  = ldap_config["port"]
    @ldap.base  = ldap_config["login_base_dn"]
    @login_attr = ldap_config["login_attr"]
  end
  
  def is_auth(login, password)
    @ldap.auth "#{@login_attr}\=#{login},#{@ldap.base}", password
    return false if !@ldap.bind
    return true
  end
end
