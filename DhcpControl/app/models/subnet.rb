class Subnet < ActiveRecord::Base
    
  belongs_to :server, :class_name => "Server", :foreign_key => "server_id"
  has_many :group
  has_many :range_ip
    
  validates_uniqueness_of :server_id, :scope => [:net]
  validate :validate_options
  
  before_save :set_netmask
  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.net.gsub!(/[ \t]/,'');
  end
  
  def self.ip_belong_net(id, ip)
    #select * from subnets where net >> '192.168.1.1'::cidr;
    where('id = ? and net >> ?', id, ip).first
  end


  def self.search_net_for_host(server_id, ip)
    where('server_id = ? and net >> ?', server_id, ip).first
  end
    
  def validate_options
    validate_dhcp_opt = ApplicationHelper::ValidateDhcpOptions.new
    result = validate_dhcp_opt.check(options)
    if result
      errors[:options] << "\"#{result}\""
    end
  end
  
  def set_netmask
    tmp_ip = IPAddress.parse(net.to_s)
    #TODO fix set is_ipv6
    self.is_ipv6   = false
    self.netmask   = tmp_ip.netmask.to_s
    self.broadcast = tmp_ip.broadcast.to_s
    self.network   = tmp_ip.network.to_s
  end
end
