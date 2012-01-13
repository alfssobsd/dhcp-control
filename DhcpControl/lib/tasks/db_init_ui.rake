namespace :db do
  desc "init web ui"
  task :init_ui => :environment do
    init_web_ui
  end
end


def init_web_ui
  user = User.new
  user.login                 = "admin"
  user.password              = "admin"
  user.password_confirmation = "admin"
  user.is_ldap               = false
  user.save

  server = Server.new
  server.name             = "Example Server"
  server.enable_ddns      = false
  server.is_authoritative = true
  server.options          = "default-lease-time 3600;\r\nmax-lease-time 86400;"
  server.save

  subnet = Subnet.new
  subnet.net         = "192.168.0.0/24"
  subnet.server_id   = server.id
  subnet.broadcast   = "192.168.0.255"
  subnet.enable_ddns = false
  subnet.netmask     = "255.255.255.0"
  subnet.options     = "option routers 192.168.17.1;\r\noption domain-name \"example.com\";\r\noption domain-name-servers 192.168.0.1, 192.168.0.2;"
  subnet.save

  range_ip = RangeIp.new
  range_ip.subnet_id = subnet.id
  range_ip.ip_start  = "192.168.0.100"
  range_ip.ip_end    = "192.168.0.200"
  range_ip.save

  group = Group.new
  group.subnet_id = subnet.id
  group.name      = "DEFAULT"
  group.default   = true
  group.save

  setting = Setting.new
  setting.enable_rabbitmq = false
  setting.rabbitmq_host   = "rabbitmq.example.org"
  setting.rabbitmq_user   = "rabbit"
  setting.rabbitmq_password = "password"
  setting.rabbitmq_vhost    = "/"
  setting.save

end