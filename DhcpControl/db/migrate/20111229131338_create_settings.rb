class CreateSettings < ActiveRecord::Migration
  def change
    create_table :settings do |t|
      t.boolean :enable_rabbitmq
      t.string :rabbitmq_host
      t.string :rabbitmq_port
      t.string :rabbitmq_vhost
      t.string :rabbitmq_user
      t.string :rabbitmq_password

      t.timestamps
    end
  end
end
