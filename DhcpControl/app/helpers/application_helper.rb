module ApplicationHelper
  dhcp_grammer_path = Rails.root.join("config/dhcp")
  Treetop.load dhcp_grammer_path.to_s

  def version
    return "0.92"
  end

  class ValidateDhcpOptions
    def initialize
      @parser = DhcpParser.new
    end
    
    def check(options)
      options.split(';').each do |option|
        option << ";"
        return option.strip if !@parser.parse(option.strip)
      end
      return nil
    end
  end
  
  class AmqpTask
    def initialize
      @setting = Setting.first
      options = {}
      options[:host]     = @setting.rabbitmq_host  || 'localhost'
      options[:port]     = @setting.rabbitmq_port  || 5672
      options[:vhost]    = @setting.rabbitmq_vhost || '/'
      options[:user]     = @setting.rabbitmq_user
      options[:pass]     = @setting.rabbitmq_password
      @client = Carrot.new(options)
    end
    
    def post_task(server_id, subnet_id, group_id, host_id, action)
      if @setting.enable_rabbitmq == true
        dhcp_q = @client.queue("task#{server_id}")

        server_id = -1 if server_id.nil?
        subnet_id = -1 if subnet_id.nil?
        group_id  = -1 if group_id.nil?
        host_id   = -1 if host_id.nil?

        task = {:action    => action,
                :server_id => server_id,
                :subnet_id => subnet_id,
                :group_id  => group_id,
                :host_id   => host_id}
        dhcp_q.publish(task.to_json)
      end
    end
  end
  
end