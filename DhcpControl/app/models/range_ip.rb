class RangeIp < ActiveRecord::Base
  belongs_to :subnet, :class_name => "Subnet", :foreign_key => "subnet_id"
  validates_uniqueness_of :subnet_id, :scope => [:ip_start, :ip_end]

  validate  :validate_ip

  def validate_ip
    begin
      subnet1 = Subnet.ip_belong_net(self.subnet_id, ip_start)
      subnet2 = Subnet.ip_belong_net(self.subnet_id, ip_end)

      if subnet1.nil?
        errors[:ip_start] << "is not belong net"
      end

      if subnet2.nil?
        errors[:ip_end] << "is not belong net"
      end

      errors[:ip_start] << "invalid" if ip_start.match(/\//)

      errors[:ip_end] << "invalid" if ip_end.match(/\//)

      if !subnet1.nil?
        net    = IPAddress.parse(subnet1.net)
        tmp_ip = IPAddress.parse("#{ip_start}/#{net.netmask}")
        if tmp_ip.broadcast.to_s == ip_start
          errors[:ip_start] << "can not be broadcast"
        end

        if tmp_ip.network.to_s == ip_start
          errors[:ip_start] << "can not be network"
        end

        if subnet1.is_ipv6
          errors[:ip_start] << "not ipv6" if tmp_ip.class != IPAddress::IPv6
        else
          errors[:ip_start] << "not ipv4" if tmp_ip.class != IPAddress::IPv4
        end
      end

      if !subnet2.nil?
        net    = IPAddress.parse(subnet2.net)
        tmp_ip = IPAddress.parse("#{ip_end}/#{net.netmask}")
        if tmp_ip.broadcast.to_s == ip_end
          errors[:ip_end] << "can not be broadcast"
        end

        if tmp_ip.network.to_s == ip_end
          errors[:ip_end] << "can not be network"
        end

        if subnet2.is_ipv6
          errors[:ip_end] << "not ipv6" if tmp_ip.class != IPAddress::IPv6
        else
          errors[:ip_end] << "not ipv4" if tmp_ip.class != IPAddress::IPv4
        end
      end
    rescue ActiveRecord::StatementInvalid => e
      errors[:ip_start] << "invalid :" + e.to_s
      errors[:ip_end] << "invalid :" + e.to_s
    end
  end
end
