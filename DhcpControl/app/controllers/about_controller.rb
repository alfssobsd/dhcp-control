class AboutController < ApplicationController
  layout 'simple_page'
  def page

    respond_to do |format|
      format.html # page.html.haml
    end
  end
end
