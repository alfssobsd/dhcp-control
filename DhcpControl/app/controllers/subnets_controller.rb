class SubnetsController < ApplicationController
  before_filter :authenticate
  # GET /subnets
  # GET /subnets.json
  def index
    @subnets = Subnet.where(:server_id => params[:server_id])
    @server  = Server.find(params[:server_id])
    
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @subnets }
    end
  end

  # GET /subnets/1
  # GET /subnets/1.json
  def show
    @subnet = Subnet.find(params[:id])
    @server = Server.find(params[:server_id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @subnet }
    end
  end

  # GET /subnets/new
  # GET /subnets/new.json
  def new
    @subnet = Subnet.new
    @server = Server.find(params[:server_id])

    
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @subnet }
    end
  end

  # GET /subnets/1/edit
  def edit
    @subnet = Subnet.find(params[:id])
    @server = Server.find(params[:server_id])
  end

  # POST /subnets
  # POST /subnets.json
  def create
    @subnet = Subnet.new(params[:subnet])
    @server = Server.find(params[:server_id])
    @subnet.server_id = params[:server_id]

    respond_to do |format|
      group  = Group.new
      if @subnet.save
        begin
          rabbit = AmqpTask.new
          group.subnet_id  = @subnet.id
          group.options    = ""
          group.default    = true
          group.name       = "DEFAULT"
          group.save
          rabbit.post_task(params[:server_id], @subnet.id, nil, nil, 'subnet:create')
          rabbit.post_task(params[:server_id], @subnet.id, group.id, nil, 'group:create')
          flash[:success] = 'Subnet was successfully created.'
          format.html { redirect_to edit_server_subnet_path(@server, @subnet) }
          format.json { render json: @subnet, status: :created, location: @subnet }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          group.delete
          @subnet.delete
          format.html { render action: "new" }
          format.json { render json: @subnet.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @subnet.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /subnets/1
  # PUT /subnets/1.json
  def update
    @subnet = Subnet.find(params[:id])
    @server = Server.find(params[:server_id])
    
    respond_to do |format|
      if @subnet.update_attributes(params[:subnet])

        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], @subnet.id, nil, nil, 'subnet:update')
          flash[:success] = 'Subnet was successfully updated.'
          format.html { redirect_to edit_server_subnet_path(@server, @subnet) }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @subnet.errors, status: :unprocessable_entity }
        end  
      else
        format.html { render action: "edit" }
        format.json { render json: @subnet.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /subnets/1
  # DELETE /subnets/1.json
  def destroy
    begin
      rabbit = AmqpTask.new
      rabbit.post_task(params[:server_id], params[:id], nil, nil, 'subnet:remove')
      @subnet = Subnet.find(params[:id])
      Host.delete_all(:subnet_id => params[:id])
      Group.delete_all(:subnet_id => params[:id])
      @subnet.destroy
    rescue Carrot::AMQP::Server::ProtocolError, Carrot::AMQP::Server::ServerDown => e
      flash[:error] = e.to_s
    end    
    respond_to do |format|
      format.html { redirect_to server_subnets_path}
      format.json { head :ok }
    end
  end
end
