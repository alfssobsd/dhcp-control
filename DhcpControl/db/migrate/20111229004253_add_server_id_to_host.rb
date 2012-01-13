class AddServerIdToHost < ActiveRecord::Migration
  def change
    add_column :hosts, :server_id, :integer
  end
end
