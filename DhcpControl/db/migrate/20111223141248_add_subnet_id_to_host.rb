class AddSubnetIdToHost < ActiveRecord::Migration
  def change
    add_column :hosts, :subnet_id, :integer
  end
end
