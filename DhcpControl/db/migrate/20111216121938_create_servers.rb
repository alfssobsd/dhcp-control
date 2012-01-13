class CreateServers < ActiveRecord::Migration
  def change
    create_table :servers do |t|
      t.string :name
      t.text :options

      t.timestamps
    end
  end
end
