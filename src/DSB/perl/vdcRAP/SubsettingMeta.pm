
package vdcRAP::SubsettingMeta;

use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);

@ISA =     qw(Exporter); 

use strict;

use Exporter; 
use vdcLOG;

$VERSION = 0.01; 

@EXPORT =qw();

sub new {

    my $self = {};

    bless $self;

#    my $logger = new_vdcLOG();
#    $self->{'logger'} = $logger; 

    return $self;
}

sub vdc_subsettingMetaFile {
    my ($self, $ddi, $fileid, $type) = @_; 

#    $colfile = $studydir . "/" . $filename . ".vlm"; 
    my $colfile = $ddi  . "." . $fileid . ".vlm"; 

    if ( -f $colfile )
    {
	# if the file exists, we want to make sure it's fresh
	# relatively to the DDI 

#	my $vlmtimestamp = (stat($colfile))[9]; 
#	my $dditimestamp = (stat($studydir . "/" . $ddi))[9]; 

#	if ( $vlmtimestamp > $dditimestamp )
#	{
#	    return $colfile; 
#	}
    }

    # if the col file doesn't exist, let's try to generate it
    # and try again:

    my ($ddi_dir, $ddi_file );

    if ( $ddi =~/^(.*)\/([^\/]*)$/ )
    {
	$ddi_dir = $1; 
	$ddi_file = $2; 
    }
    else
    {
	$ddi_dir = '/tmp/VDC'; 
	$ddi_file = $ddi; 
    }

    return $self->vdc_generateColFile ( $ddi_file, $ddi_dir, $fileid, $type ); 
}

sub vdc_generateColFile {
    my ($self, $ddi, $dir, $dataset_id, $type) = @_; 

#    $self->{'logger'}->vdcLOG_info ( "VDC::vdcRAP::SubsettingMeta", 'generateColFile', join ( ":", $ddi, $dir, $datafile, $type ) ); 

#    open ( DDI, $dir . "/" . $ddi ) || return undef; 

#    my $dataset_id = ""; 
#    my $i = 0; 
    
#    while ( <DDI> )
#    {
#	if ( /<fileDscr.*ID=\"([^\"]*)\".*\/Access.*\/([^\/\"]*)\"/ )
#        {
#	    $i++; 
#
#	    if ( $2 eq $datafile )
#	    {
#		$dataset_id = $1;
#		last; 
#  	    }
#        }
#   }
#   close DDI; 

#    $self->{'logger'}->vdcLOG_info ( "VDC::vdcRAP::SubsettingMeta", 'generateColFile', 'this is dataset ' . $dataset_id ); 


    return undef unless $dataset_id; 

    system ( "/usr/local/VDC/sbin/recreatevlm.pl $dir $ddi $dataset_id $type" ); 

    my $col_filename = sprintf ( "%s/%s.vlm", $dir, $dataset_id );
 
    return undef unless -f $col_filename; 

    my $new_col_filename = sprintf ( "%s/%s.%s.vlm", $dir, $ddi, $dataset_id ); 

    system ( "mv -f $col_filename $new_col_filename" ); 
   
    return $new_col_filename; 
}

1;
