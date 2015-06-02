DROP PROCEDURE IF EXISTS dump_images;
DELIMITER $$
CREATE PROCEDURE dump_images()
  BEGIN
    DECLARE client_id INT;
    DECLARE crsr CURSOR FOR SELECT clph.client_id FROM image_info ii, client_photo clph, customer_picture cp WHERE ii.length > 0 AND clph.image_info = ii.image_id AND cp.picture_id = ii.customer_picture_entity;
    OPEN crsr;
    read_loop: LOOP
      FETCH crsr INTO client_id;
      SET @query = concat('SELECT cp.picture FROM image_info ii, client_photo clph, customer_picture cp WHERE clph.image_info = ii.image_id AND cp.picture_id = ii.customer_picture_entity AND clph.client_id = ',
                          client_id, ' INTO DUMPFIlE "/tmp/mifos-legacy/', client_id,'.jpg"');
      PREPARE write_file FROM @query;
      EXECUTE write_file;
    END LOOP ;
    CLOSE crsr;
  END $$
DELIMITER ;