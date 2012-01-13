class CreateSubnets < ActiveRecord::Migration
  def change
    create_table :subnets do |t|
      t.integer :server_id
      t.column :net, :cidr
      t.text :options

      t.timestamps
    end
  end
end
