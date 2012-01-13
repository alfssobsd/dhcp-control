class SessionsController < ApplicationController
  layout 'login'
  
  def new
    
    if signed_in?
      return redirect_to root_path
    end
  end
  
  def create
    user = nil
    
    begin
      user = User.authenticate(params[:session][:login],
                               params[:session][:password])
    rescue Net::LDAP::LdapError => error
      logger.error { "ERROR LDAP: %s" % error }
    end
    
    if user.nil?
      flash.now[:error] = "Invalid login/password combination."
      @title = "Sign in"
      render 'new'
    else
      sign_in user
      redirect_back_or root_path
    end
  end

  def destroy
    sign_out
    redirect_to root_path
  end
end
