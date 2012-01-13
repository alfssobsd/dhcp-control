class DdnsZonesController < ApplicationController
  # GET /ddns_zones
  # GET /ddns_zones.json
  def index
    @ddns_zones = DdnsZone.where(:subnet_id => params[:subnet_id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @ddns_zones }
    end
  end

  # GET /ddns_zones/1
  # GET /ddns_zones/1.json
  def show
    @ddns_zone = DdnsZone.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @ddns_zone }
    end
  end

  # GET /ddns_zones/new
  # GET /ddns_zones/new.json
  def new
    @ddns_zone = DdnsZone.new
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @ddns_zone }
    end
  end

  # GET /ddns_zones/1/edit
  def edit
    @ddns_zone = DdnsZone.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
  end

  # POST /ddns_zones
  # POST /ddns_zones.json
  def create
    @ddns_zone = DdnsZone.new(params[:ddns_zone])
    @ddns_zone.subnet_id = params[:subnet_id]
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
    @subnet.enable_ddns = true

    respond_to do |format|
      if @ddns_zone.save
        @subnet.save
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')

          flash[:success] = 'DDNS Zone was successfully created.'
          format.html { redirect_to edit_server_subnet_ddns_zone_path(@server, @subnet, @ddns_zone) }
          format.json { render json: @ddns_zone, status: :created, location: @ddns_zone }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "new" }
          format.json { render json: @ddns_zone.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @ddns_zone.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /ddns_zones/1
  # PUT /ddns_zones/1.json
  def update
    @ddns_zone = DdnsZone.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      if @ddns_zone.update_attributes(params[:ddns_zone])
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')

          flash[:success] = 'DDNS Zone was successfully updated.'
          format.html { redirect_to edit_server_subnet_ddns_zone_path(@server, @subnet, @ddns_zone) }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @ddns_zone.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "edit" }
        format.json { render json: @ddns_zone.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /ddns_zones/1
  # DELETE /ddns_zones/1.json
  def destroy
    @ddns_zone = DdnsZone.find(params[:id])
    @ddns_zone.destroy

    if DdnsZone.where(:subnet_id => params[:subnet_id]).count < 1
      @subnet = Subnet.find(params[:subnet_id])
      @subnet.enable_ddns = false
      @subnet.save
    end

    begin
      rabbit = AmqpTask.new
      rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')
    rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
      flash[:error] = e.to_s
    end

    respond_to do |format|
      format.html { redirect_to server_subnet_ddns_zones_path }
      format.json { head :ok }
    end
  end
end
