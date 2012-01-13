class SettingsController < ApplicationController
layout 'blank'

  # GET /settings
  # GET /settings.json
  def index
    @settings = Setting.first

    respond_to do |format|
      format.html { redirect_to edit_setting_path(@settings)}
      format.json { render json: ok}
    end
  end

  # GET /settings/1/edit
  def edit
    @setting = Setting.find(params[:id])
  end

  # PUT /settings/1
  # PUT /settings/1.json
  def update
    @setting = Setting.find(params[:id])

    respond_to do |format|
      if @setting.update_attributes(params[:setting])
        flash[:success] = 'Setting was successfully updated.'
        format.html { redirect_to edit_setting_path(@setting) }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @setting.errors, status: :unprocessable_entity }
      end
    end
  end

end
