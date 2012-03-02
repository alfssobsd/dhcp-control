class HostsController < ApplicationController
  before_filter :authenticate
  protect_from_forgery :except => [:quick_create_api, :quick_destory_api]
  
  # GET /hosts
  # GET /hosts.json
  def index
    @hosts = Host.where(:group_id => params[:group_id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @hosts }
    end
  end

  # GET /hosts/1
  # GET /hosts/1.json
  def show
    @host = Host.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @host }
    end
  end

  # GET /hosts/new
  # GET /hosts/new.json
  def new
    @host = Host.new
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @host }
    end
  end

  # GET /hosts/1/edit
  def edit
    @host = Host.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])
  end

  def quick_new
    @host   = Host.new
    @server = Server.find(params[:server_id])

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @host }
    end
  end

  def quick_create_api
    @host      = Host.new()
    @host.ip   = params[:ip]
    @host.mac  = params[:mac]
    @host.name = params[:name]
    @server    = Server.find(params[:server_id])
    @subnet    = Subnet.search_net_for_host(@server.id, @host.ip)

    if !@subnet.nil?
      @group = Group.where("subnet_id = ? AND groups.default = ?", @subnet.id, true).first
      @host.group_id  = @group.id
      @host.server_id = @server.id
      @host.subnet_id = @subnet.id
    end

    respond_to do |format|
      if !@subnet.nil? and @host.save
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(@host.server_id, @host.subnet_id, @host.group_id, nil, 'group:update')
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          format.json { render json: @host.errors,  status: :unprocessable_entity  }
        end
      else
        format.json { render json: @host.errors,  status: :unprocessable_entity  }
      end
    end
  end

  def quick_create
    @host   = Host.new(params[:host])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.search_net_for_host(@server.id, @host.ip)

    if !@subnet.nil?
      @group = Group.where("subnet_id = ? AND groups.default = ?", @subnet.id, true).first
      @host.group_id  = @group.id
      @host.server_id = @server.id
      @host.subnet_id = @subnet.id
    end

    respond_to do |format|
      if !@subnet.nil? and @host.save
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(@host.server_id, @host.subnet_id, @host.group_id, nil, 'group:update')
          flash[:success] = "Host was successfully created. #{@subnet.net} #{@host.ip} #{@host.mac}"

          format.html { redirect_to server_quick_new_host_path(@server.id) }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "quick_new" }
          format.json { render json: @host.errors,  status: :unprocessable_entity  }
        end
      else
        if @subnet.nil?
          flash[:error] = "Now found subnet for Host: #{@host.ip}"
        end
        format.html { render action: "quick_new" }
        format.json { render json: @host.errors,  status: :unprocessable_entity  }
      end
    end
  end

  # POST /hosts
  # POST /hosts.json
  def create
    @host   = Host.new(params[:host])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])
    @host.group_id  = @group.id
    @host.subnet_id = @group.subnet_id
    @host.server_id = @group.subnet.server_id

    respond_to do |format|
      if @host.save
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], params[:group_id], nil, 'group:update')
          flash[:success] = 'Host was successfully created.'

          format.html { redirect_to edit_server_subnet_group_host_path(@server, @subnet, @group, @host) }
          format.json { render json: @host, status: :created, location: @host }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "new" }
          format.json { render json: @host.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @host.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /hosts/1
  # PUT /hosts/1.json
  def update
    @host = Host.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @group  = Group.find(params[:group_id])
    
    respond_to do |format|
      if @host.update_attributes(params[:host])
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], params[:group_id], nil, 'group:update')
          flash[:success] = 'Host was successfully updated.'

          format.html { redirect_to edit_server_subnet_group_host_path(@server, @subnet, @group, @host) }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @host.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "edit" }
        format.json { render json: @host.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /hosts/1
  # DELETE /hosts/1.json
  def destroy
    @host = Host.find(params[:id])
    @host.destroy

    begin
      rabbit = AmqpTask.new
      rabbit.post_task(params[:server_id], params[:subnet_id], params[:group_id], nil, 'group:update')
    rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
      flash[:error] = e.to_s
    end
    respond_to do |format|
      format.html { redirect_to server_subnet_group_hosts_path }
      format.json { head :ok }
    end
  end


  def quick_destory
    @host = Host.where("ip = ? and server_id = ?", params[:ip], params[:server_id]).first
    if @host != nil
      @host.destroy 
      begin
        rabbit = AmqpTask.new
        rabbit.post_task(@host.server_id, @host.subnet_id, @host.group_id, nil, 'group:update')
      rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
        flash[:error] = e.to_s
      end
    end
    respond_to do |format|
      format.html { redirect_to server_subnet_group_hosts_path }
      format.json { head :ok }
    end
  end
  
  def quick_destory_api
    @host = Host.where("ip = ? and server_id = ?", params[:ip], params[:server_id]).first
    if @host != nil
      @host.destroy 
      begin
        rabbit = AmqpTask.new
        rabbit.post_task(@host.server_id, @host.subnet_id, @host.group_id, nil, 'group:update')
      rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
        flash[:error] = e.to_s
      end
    end
    respond_to do |format|
      format.json { head :ok }
    end
  end
end
