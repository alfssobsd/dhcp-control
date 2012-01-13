class RangeIpsController < ApplicationController
  # GET /range_ips
  # GET /range_ips.json
  def index
    @range_ips = RangeIp.where(:subnet_id => params[:subnet_id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @range_ips }
    end
  end

  # GET /range_ips/1
  # GET /range_ips/1.json
  def show
    @range_ip = RangeIp.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @range_ip }
    end
  end

  # GET /range_ips/new
  # GET /range_ips/new.json
  def new
    @range_ip = RangeIp.new
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @range_ip }
    end
  end

  # GET /range_ips/1/edit
  def edit
    @range_ip = RangeIp.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
  end

  # POST /range_ips
  # POST /range_ips.json
  def create
    @range_ip = RangeIp.new(params[:range_ip])
    @range_ip.subnet_id = params[:subnet_id]
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      if @range_ip.save
        rabbit = AmqpTask.new
        rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')
        flash[:success] = 'Range ip was successfully created.'
        format.html { redirect_to edit_server_subnet_range_ip_path(@server, @subnet, @range_ip) }
        format.json { render json: @range_ip, status: :created, location: @range_ip }
      else
        format.html { render action: "new" }
        format.json { render json: @range_ip.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /range_ips/1
  # PUT /range_ips/1.json
  def update
    @range_ip = RangeIp.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      if @range_ip.update_attributes(params[:range_ip])
        rabbit = AmqpTask.new
        rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')
        flash[:success] = 'Range ip was successfully updated.'
        format.html { redirect_to edit_server_subnet_range_ip_path(@server, @subnet, @range_ip) }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @range_ip.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /range_ips/1
  # DELETE /range_ips/1.json
  def destroy
    @range_ip = RangeIp.find(params[:id])
    @range_ip.destroy
    rabbit = AmqpTask.new
    rabbit.post_task(params[:server_id], params[:subnet_id], nil, nil, 'subnet:update')
    
    respond_to do |format|
      format.html { redirect_to server_subnet_range_ips_path }
      format.json { head :ok }
    end
  end
end
