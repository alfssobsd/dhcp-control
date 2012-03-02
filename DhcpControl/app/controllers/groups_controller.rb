class GroupsController < ApplicationController
  before_filter :authenticate
  
  # GET /groups
  # GET /groups.json
  def index
    @groups = Group.where(:subnet_id => params[:subnet_id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @groups }
    end
  end

  # GET /groups/1
  # GET /groups/1.json
  def show
    @group = Group.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @group }
    end
  end

  # GET /groups/new
  # GET /groups/new.json
  def new
    @group = Group.new
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @group }
    end
  end

  # GET /groups/1/edit
  def edit
    @group = Group.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])
  end

  # POST /groups
  # POST /groups.json
  def create
    @group = Group.new(params[:group])
    @group.subnet_id  = params[:subnet_id]
    @group.default = false
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      if @group.save
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], @group.id, nil, 'group:create')
          flash[:success] = 'Group was successfully created.'

          format.html { redirect_to edit_server_subnet_group_path(@server, @subnet, @group) }
          format.json { render json: @group, status: :created, location: @group }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "new" }
          format.json { render json: @group.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /groups/1
  # PUT /groups/1.json
  def update
    @group = Group.find(params[:id])
    @server = Server.find(params[:server_id])
    @subnet = Subnet.find(params[:subnet_id])

    respond_to do |format|
      if @group.update_attributes(params[:group])
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], params[:subnet_id], @group.id, nil, 'group:update')
          flash[:success] = 'Group was successfully updated.'

          format.html { redirect_to edit_server_subnet_group_path(@server, @subnet, @group) }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @group.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "edit" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /groups/1
  # DELETE /groups/1.json
  def destroy
    @group = Group.find(params[:id])
    if @group.default
      flash[:error] = "You cannot delete default group: #{@group.name}"
    else
      @group.destroy
    end

    begin
      rabbit = AmqpTask.new
      rabbit.post_task(params[:server_id], params[:subnet_id], @group.id, nil, 'group:update')
    rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
      flash[:error] = e.to_s
    end
    respond_to do |format|
      format.html { redirect_to server_subnet_groups_path }
      format.json { head :ok }
    end
  end
end
