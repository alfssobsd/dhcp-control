class AddIsIpv6ToSubnet < ActiveRecord::Migration
  def change
    add_column :subnets, :is_ipv6, :boolean, :dafault => false
  end
end
