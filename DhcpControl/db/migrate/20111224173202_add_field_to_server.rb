class AddFieldToServer < ActiveRecord::Migration
  def change
    add_column :servers, :is_authoritative, :boolean
    add_column :servers, :enable_ddns, :boolean
  end
end
