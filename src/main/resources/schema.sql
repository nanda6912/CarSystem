-- Smart Parking System PostgreSQL Database Schema
-- Compatible with PostgreSQL 18.2

-- Create database if it doesn't exist (run this manually in psql)
-- CREATE DATABASE IF NOT EXISTS smart_parking_db;

-- Drop existing tables if they exist (for clean restart)
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS parking_slots CASCADE;

-- Create parking_slots table
CREATE TABLE parking_slots (
    slot_id VARCHAR(20) PRIMARY KEY,
    floor VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'EMPTY',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    booking_id VARCHAR(50) UNIQUE NOT NULL,
    slot_id VARCHAR(20) NOT NULL,
    vehicle_number VARCHAR(50) NOT NULL,
    owner_name VARCHAR(100),
    phone_number VARCHAR(20) NOT NULL,
    vehicle_type VARCHAR(20) DEFAULT 'CAR',
    entry_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exit_time TIMESTAMP,
    amount_charged DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_vehicle_type CHECK (vehicle_type IN ('CAR', 'BIKE')),
    CONSTRAINT chk_amount CHECK (amount_charged >= 0)
);

-- Create indexes for better performance
CREATE INDEX idx_parking_slots_status ON parking_slots(status);
CREATE INDEX idx_parking_slots_floor ON parking_slots(floor);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_vehicle_number ON bookings(vehicle_number);
CREATE INDEX idx_bookings_slot_id ON bookings(slot_id);
CREATE INDEX idx_bookings_entry_time ON bookings(entry_time);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to tables
CREATE TRIGGER update_parking_slots_updated_at 
    BEFORE UPDATE ON parking_slots 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bookings_updated_at 
    BEFORE UPDATE ON bookings 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Initialize parking slots (200 slots total)
INSERT INTO parking_slots (slot_id, floor, status) VALUES
-- Ground Floor (Block A-E, 20 slots each)
('GA001', 'Ground', 'EMPTY'), ('GA002', 'Ground', 'EMPTY'), ('GA003', 'Ground', 'EMPTY'), ('GA004', 'Ground', 'EMPTY'),
('GA005', 'Ground', 'EMPTY'), ('GA006', 'Ground', 'EMPTY'), ('GA007', 'Ground', 'EMPTY'), ('GA008', 'Ground', 'EMPTY'),
('GA009', 'Ground', 'EMPTY'), ('GA010', 'Ground', 'EMPTY'), ('GA011', 'Ground', 'EMPTY'), ('GA012', 'Ground', 'EMPTY'),
('GA013', 'Ground', 'EMPTY'), ('GA014', 'Ground', 'EMPTY'), ('GA015', 'Ground', 'EMPTY'), ('GA016', 'Ground', 'EMPTY'),
('GA017', 'Ground', 'EMPTY'), ('GA018', 'Ground', 'EMPTY'), ('GA019', 'Ground', 'EMPTY'), ('GA020', 'Ground', 'EMPTY'),

('GB001', 'Ground', 'EMPTY'), ('GB002', 'Ground', 'EMPTY'), ('GB003', 'Ground', 'EMPTY'), ('GB004', 'Ground', 'EMPTY'),
('GB005', 'Ground', 'EMPTY'), ('GB006', 'Ground', 'EMPTY'), ('GB007', 'Ground', 'EMPTY'), ('GB008', 'Ground', 'EMPTY'),
('GB009', 'Ground', 'EMPTY'), ('GB010', 'Ground', 'EMPTY'), ('GB011', 'Ground', 'EMPTY'), ('GB012', 'Ground', 'EMPTY'),
('GB013', 'Ground', 'EMPTY'), ('GB014', 'Ground', 'EMPTY'), ('GB015', 'Ground', 'EMPTY'), ('GB016', 'Ground', 'EMPTY'),
('GB017', 'Ground', 'EMPTY'), ('GB018', 'Ground', 'EMPTY'), ('GB019', 'Ground', 'EMPTY'), ('GB020', 'Ground', 'EMPTY'),

('GC001', 'Ground', 'EMPTY'), ('GC002', 'Ground', 'EMPTY'), ('GC003', 'Ground', 'EMPTY'), ('GC004', 'Ground', 'EMPTY'),
('GC005', 'Ground', 'EMPTY'), ('GC006', 'Ground', 'EMPTY'), ('GC007', 'Ground', 'EMPTY'), ('GC008', 'Ground', 'EMPTY'),
('GC009', 'Ground', 'EMPTY'), ('GC010', 'Ground', 'EMPTY'), ('GC011', 'Ground', 'EMPTY'), ('GC012', 'Ground', 'EMPTY'),
('GC013', 'Ground', 'EMPTY'), ('GC014', 'Ground', 'EMPTY'), ('GC015', 'Ground', 'EMPTY'), ('GC016', 'Ground', 'EMPTY'),
('GC017', 'Ground', 'EMPTY'), ('GC018', 'Ground', 'EMPTY'), ('GC019', 'Ground', 'EMPTY'), ('GC020', 'Ground', 'EMPTY'),

('GD001', 'Ground', 'EMPTY'), ('GD002', 'Ground', 'EMPTY'), ('GD003', 'Ground', 'EMPTY'), ('GD004', 'Ground', 'EMPTY'),
('GD005', 'Ground', 'EMPTY'), ('GD006', 'Ground', 'EMPTY'), ('GD007', 'Ground', 'EMPTY'), ('GD008', 'Ground', 'EMPTY'),
('GD009', 'Ground', 'EMPTY'), ('GD010', 'Ground', 'EMPTY'), ('GD011', 'Ground', 'EMPTY'), ('GD012', 'Ground', 'EMPTY'),
('GD013', 'Ground', 'EMPTY'), ('GD014', 'Ground', 'EMPTY'), ('GD015', 'Ground', 'EMPTY'), ('GD016', 'Ground', 'EMPTY'),
('GD017', 'Ground', 'EMPTY'), ('GD018', 'Ground', 'EMPTY'), ('GD019', 'Ground', 'EMPTY'), ('GD020', 'Ground', 'EMPTY'),

('GE001', 'Ground', 'EMPTY'), ('GE002', 'Ground', 'EMPTY'), ('GE003', 'Ground', 'EMPTY'), ('GE004', 'Ground', 'EMPTY'),
('GE005', 'Ground', 'EMPTY'), ('GE006', 'Ground', 'EMPTY'), ('GE007', 'Ground', 'EMPTY'), ('GE008', 'Ground', 'EMPTY'),
('GE009', 'Ground', 'EMPTY'), ('GE010', 'Ground', 'EMPTY'), ('GE011', 'Ground', 'EMPTY'), ('GE012', 'Ground', 'EMPTY'),
('GE013', 'Ground', 'EMPTY'), ('GE014', 'Ground', 'EMPTY'), ('GE015', 'Ground', 'EMPTY'), ('GE016', 'Ground', 'EMPTY'),
('GE017', 'Ground', 'EMPTY'), ('GE018', 'Ground', 'EMPTY'), ('GE019', 'Ground', 'EMPTY'), ('GE020', 'Ground', 'EMPTY'),

-- First Floor (Block A-E, 20 slots each)
('FA001', 'First', 'EMPTY'), ('FA002', 'First', 'EMPTY'), ('FA003', 'First', 'EMPTY'), ('FA004', 'First', 'EMPTY'),
('FA005', 'First', 'EMPTY'), ('FA006', 'First', 'EMPTY'), ('FA007', 'First', 'EMPTY'), ('FA008', 'First', 'EMPTY'),
('FA009', 'First', 'EMPTY'), ('FA010', 'First', 'EMPTY'), ('FA011', 'First', 'EMPTY'), ('FA012', 'First', 'EMPTY'),
('FA013', 'First', 'EMPTY'), ('FA014', 'First', 'EMPTY'), ('FA015', 'First', 'EMPTY'), ('FA016', 'First', 'EMPTY'),
('FA017', 'First', 'EMPTY'), ('FA018', 'First', 'EMPTY'), ('FA019', 'First', 'EMPTY'), ('FA020', 'First', 'EMPTY'),

('FB001', 'First', 'EMPTY'), ('FB002', 'First', 'EMPTY'), ('FB003', 'First', 'EMPTY'), ('FB004', 'First', 'EMPTY'),
('FB005', 'First', 'EMPTY'), ('FB006', 'First', 'EMPTY'), ('FB007', 'First', 'EMPTY'), ('FB008', 'First', 'EMPTY'),
('FB009', 'First', 'EMPTY'), ('FB010', 'First', 'EMPTY'), ('FB011', 'First', 'EMPTY'), ('FB012', 'First', 'EMPTY'),
('FB013', 'First', 'EMPTY'), ('FB014', 'First', 'EMPTY'), ('FB015', 'First', 'EMPTY'), ('FB016', 'First', 'EMPTY'),
('FB017', 'First', 'EMPTY'), ('FB018', 'First', 'EMPTY'), ('FB019', 'First', 'EMPTY'), ('FB020', 'First', 'EMPTY'),

('FC001', 'First', 'EMPTY'), ('FC002', 'First', 'EMPTY'), ('FC003', 'First', 'EMPTY'), ('FC004', 'First', 'EMPTY'),
('FC005', 'First', 'EMPTY'), ('FC006', 'First', 'EMPTY'), ('FC007', 'First', 'EMPTY'), ('FC008', 'First', 'EMPTY'),
('FC009', 'First', 'EMPTY'), ('FC010', 'First', 'EMPTY'), ('FC011', 'First', 'EMPTY'), ('FC012', 'First', 'EMPTY'),
('FC013', 'First', 'EMPTY'), ('FC014', 'First', 'EMPTY'), ('FC015', 'First', 'EMPTY'), ('FC016', 'First', 'EMPTY'),
('FC017', 'First', 'EMPTY'), ('FC018', 'First', 'EMPTY'), ('FC019', 'First', 'EMPTY'), ('FC020', 'First', 'EMPTY'),

('FD001', 'First', 'EMPTY'), ('FD002', 'First', 'EMPTY'), ('FD003', 'First', 'EMPTY'), ('FD004', 'First', 'EMPTY'),
('FD005', 'First', 'EMPTY'), ('FD006', 'First', 'EMPTY'), ('FD007', 'First', 'EMPTY'), ('FD008', 'First', 'EMPTY'),
('FD009', 'First', 'EMPTY'), ('FD010', 'First', 'EMPTY'), ('FD011', 'First', 'EMPTY'), ('FD012', 'First', 'EMPTY'),
('FD013', 'First', 'EMPTY'), ('FD014', 'First', 'EMPTY'), ('FD015', 'First', 'EMPTY'), ('FD016', 'First', 'EMPTY'),
('FD017', 'First', 'EMPTY'), ('FD018', 'First', 'EMPTY'), ('FD019', 'First', 'EMPTY'), ('FD020', 'First', 'EMPTY'),

('FE001', 'First', 'EMPTY'), ('FE002', 'First', 'EMPTY'), ('FE003', 'First', 'EMPTY'), ('FE004', 'First', 'EMPTY'),
('FE005', 'First', 'EMPTY'), ('FE006', 'First', 'EMPTY'), ('FE007', 'First', 'EMPTY'), ('FE008', 'First', 'EMPTY'),
('FE009', 'First', 'EMPTY'), ('FE010', 'First', 'EMPTY'), ('FE011', 'First', 'EMPTY'), ('FE012', 'First', 'EMPTY'),
('FE013', 'First', 'EMPTY'), ('FE014', 'First', 'EMPTY'), ('FE015', 'First', 'EMPTY'), ('FE016', 'First', 'EMPTY'),
('FE017', 'First', 'EMPTY'), ('FE018', 'First', 'EMPTY'), ('FE019', 'First', 'EMPTY'), ('FE020', 'First', 'EMPTY');

-- Create view for parking statistics
CREATE OR REPLACE VIEW parking_stats AS
SELECT 
    COUNT(*) as total_slots,
    COUNT(CASE WHEN status = 'EMPTY' THEN 1 END) as available_slots,
    COUNT(CASE WHEN status = 'OCCUPIED' THEN 1 END) as occupied_slots,
    ROUND(COUNT(CASE WHEN status = 'OCCUPIED' THEN 1 END) * 100.0 / COUNT(*), 2) as occupancy_percentage
FROM parking_slots;

-- Create view for booking statistics
CREATE OR REPLACE VIEW booking_stats AS
SELECT 
    COUNT(*) as total_bookings,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_bookings,
    COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_bookings,
    COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled_bookings,
    COALESCE(SUM(amount_charged), 0) as total_revenue,
    COALESCE(AVG(amount_charged), 0) as average_amount
FROM bookings;

-- Grant permissions (adjust username as needed)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

COMMIT;
