class DdnsKeysController < ApplicationController
  # GET /ddns_keys
  # GET /ddns_keys.json
  def index
    @ddns_keys = DdnsKey.where(:server_id => params[:server_id])
    @server    = Server.find(params[:server_id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @ddns_keys }
    end
  end

  # GET /ddns_keys/1
  # GET /ddns_keys/1.json
  def show
    @ddns_key = DdnsKey.find(params[:id])
    @server   = Server.find(params[:server_id])
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @ddns_key }
    end
  end

  # GET /ddns_keys/new
  # GET /ddns_keys/new.json
  def new
    @ddns_key = DdnsKey.new
    @server   = Server.find(params[:server_id])
    
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @ddns_key }
    end
  end

  # GET /ddns_keys/1/edit
  def edit
    @ddns_key = DdnsKey.find(params[:id])
    @server   = Server.find(params[:server_id])
  end

  # POST /ddns_keys
  # POST /ddns_keys.json
  def create
    @ddns_key = DdnsKey.new(params[:ddns_key])
    @ddns_key.server_id = params[:server_id]
    @server   = Server.find(params[:server_id])

    respond_to do |format|
      if @ddns_key.save

        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], nil, nil, nil, 'server:update')
          flash[:success] = 'Ddns key was successfully created.'

          format.html { redirect_to edit_server_ddns_key_path(@server, @ddns_key)}
          format.json { render json: @ddns_key, status: :created, location: @ddns_key }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "new" }
          format.json { render json: @ddns_key.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "new" }
        format.json { render json: @ddns_key.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /ddns_keys/1
  # PUT /ddns_keys/1.json
  def update
    @ddns_key = DdnsKey.find(params[:id])
    @server   = Server.find(params[:server_id])

    respond_to do |format|
      if @ddns_key.update_attributes(params[:ddns_key])

        begin
          rabbit = AmqpTask.new
          rabbit.post_task(params[:server_id], nil, nil, nil, 'server:update')
          flash[:success] = 'Ddns key was successfully updated.'

          format.html { redirect_to edit_server_ddns_key_path(@server, @ddns_key)}
          format.json { head :ok }
        rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
          flash[:error] = e.to_s
          format.html { render action: "edit" }
          format.json { render json: @ddns_key.errors, status: :unprocessable_entity }
        end
      else
        format.html { render action: "edit" }
        format.json { render json: @ddns_key.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /ddns_keys/1
  # DELETE /ddns_keys/1.json
  def destroy
    @ddns_key = DdnsKey.find(params[:id])
    @ddns_key.destroy

    begin
      rabbit = AmqpTask.new
      rabbit.post_task(params[:server_id], nil, nil, nil, 'server:update')
    rescue Carrot::AMQP::Server::ServerDown, Carrot::AMQP::Server::ProtocolError => e
      flash[:error] = e.to_s
    end
    
    respond_to do |format|
      format.html { redirect_to server_ddns_keys_path }
      format.json { head :ok }
    end
  end
end
