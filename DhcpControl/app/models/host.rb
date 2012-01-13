class Host < ActiveRecord::Base
  belongs_to :group, :class_name => "Group", :foreign_key => "group_id"

  validates :server_id, :presence => true
  validates :subnet_id, :presence => true
  validates :group_id, :presence => true
  validates :name, :presence => true
  validates :ip,   :presence => true
  validates :mac,  :presence => true
  
  validates_uniqueness_of :mac,  :scope => [:subnet_id]
  validates_uniqueness_of :ip,   :scope => [:subnet_id]
  validates_uniqueness_of :name, :scope => [:server_id]
  
  validate  :validate_ip
  validate  :validate_mac
  
  def validate_mac
    if !mac.nil?
      
      if (mac =~ /^([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}$/i).nil?
         errors[:mac] << "is invalid address"
      end
      
    end
  end
  
  def validate_ip
    begin
      group  = Group.find(group_id)
      subnet = Subnet.ip_belong_net(group.subnet_id, ip)
      
      if subnet.nil?
        errors[:ip] << "is not belong net"
      end
    
      if ip.match(/\//)
        errors[:ip] << "invalid"
      end
    
      if !subnet.nil?
        net    = IPAddress.parse(subnet.net)
        tmp_ip = IPAddress.parse("#{ip}/#{net.netmask}")
        if tmp_ip.broadcast.to_s == ip
          errors[:ip] << "can not be broadcast"
        end

        if tmp_ip.network.to_s == ip
          errors[:ip] << "can not be network"
        end
      
        if subnet.is_ipv6
          errors[:ip] << "not ipv6" if tmp_ip.class != IPAddress::IPv6
        else
          errors[:ip] << "not ipv4" if tmp_ip.class != IPAddress::IPv4
        end
      end
    rescue ActiveRecord::StatementInvalid => e
      errors[:ip] << "invalid :" + e.to_s 
    end
  end
    
end
