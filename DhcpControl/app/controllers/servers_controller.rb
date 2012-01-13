class ServersController < ApplicationController
  before_filter :authenticate
  
  # GET /servers
  # GET /servers.json
  def index
    @servers = Server.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @servers }
    end
  end

  # GET /servers/1
  # GET /servers/1.json
  def show
    @server = Server.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @server }
    end
  end

  # GET /servers/new
  # GET /servers/new.json
  def new
    @server = Server.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @server }
    end
  end

  # GET /servers/1/edit
  def edit
    @server = Server.find(params[:id])
  end

  # POST /servers
  # POST /servers.json
  def create
    @server = Server.new(params[:server])

    respond_to do |format|
      if @server.save

        begin
          rabbit = AmqpTask.new
          rabbit.post_task(@server.id, nil, nil, nil, 'server:create')
          flash[:success] = 'Server was successfully created.'

          format.html { redirect_to servers_url }
          format.json { render json: @server, status: :created, location: @server }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "new" }
          format.json { render json: @server.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @server.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /servers/1
  # PUT /servers/1.json
  def update
    @server = Server.find(params[:id])

    respond_to do |format|
      if @server.update_attributes(params[:server])
        begin
          rabbit = AmqpTask.new
          rabbit.post_task(@server.id, nil, nil, nil, 'server:update')
          flash[:success] = 'Server was successfully updated.'

          format.html { redirect_to servers_url }
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @server.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "edit" }
        format.json { render json: @server.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /servers/1
  # DELETE /servers/1.json
  def destroy
    @server = Server.find(params[:id])
    @server.destroy
    
    rabbit = AmqpTask.new
    rabbit.post_task(@server.id, nil, nil, nil, 'server:remove')
    
    respond_to do |format|
      format.html { redirect_to servers_url }
      format.json { head :ok }
    end
  end
end
