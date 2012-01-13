class AddEnableDdnsToSubnet < ActiveRecord::Migration
  def change
    add_column :subnets, :enable_ddns, :boolean, :default => false
  end
end
