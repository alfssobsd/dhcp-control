class CreateRangeIps < ActiveRecord::Migration
  def change
    create_table :range_ips do |t|
      t.integer :subnet_id
      t.string :ip_start
      t.string :ip_end

      t.timestamps
    end
  end
end
